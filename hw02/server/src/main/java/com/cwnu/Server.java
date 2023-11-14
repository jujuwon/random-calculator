package com.cwnu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	private static final int PORT = 8000;
	private static final int MAX_CONNECT_COUNT = 4;
	private static final int ROUND_COUNT = 100;
	private static final AtomicInteger CURRENT_ROUND = new AtomicInteger(0);
	private static final AtomicInteger CONNECTED_SOCKET_COUNT = new AtomicInteger(0);
	private static final AtomicInteger SYSTEM_CLOCK = new AtomicInteger(0);

	private static final int MATRIX_SIZE = 10;
	private static final String LOG_FILE = "../log/Server.txt";
	private static ServerSocket serverSocket;
	private final ExecutorService pool = Executors.newFixedThreadPool(MAX_CONNECT_COUNT);
	private static final List<Map<Set<String>, int[][]>> results = new ArrayList<>();
	private final List<Socket> clientSockets = new ArrayList<>(MAX_CONNECT_COUNT);
	private static List<ClientHandler> handlers = new ArrayList<>(MAX_CONNECT_COUNT);

	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
		for (int i = 0; i < ROUND_COUNT; i++) {
			Map<Set<String>, int[][]>result = new HashMap<>();
			result.put(new HashSet<>(Set.of("1", "2")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			result.put(new HashSet<>(Set.of("1", "3")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			result.put(new HashSet<>(Set.of("1", "4")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			result.put(new HashSet<>(Set.of("2", "3")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			result.put(new HashSet<>(Set.of("2", "4")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			result.put(new HashSet<>(Set.of("3", "4")), new int[MATRIX_SIZE][MATRIX_SIZE]);
			results.add(result);
		}
	}

	public void start() throws IOException {
		while (CONNECTED_SOCKET_COUNT.get() < MAX_CONNECT_COUNT) {
			clientSockets.add(serverSocket.accept());
			CONNECTED_SOCKET_COUNT.getAndIncrement();
			log("Client " + CONNECTED_SOCKET_COUNT.get() + " connected.");
		}
		while (CURRENT_ROUND.get() < ROUND_COUNT) {
			int round = CURRENT_ROUND.incrementAndGet();
			log("Round " + (round) + " started.");

			// 라운드마다 쓰레드를 만들어서 클라이언트에게 연산 작업 할당, 결과 받아서 처리.
			List<Thread> threads = new ArrayList<>();
			for (int i = 0; i < clientSockets.size(); i++) {
				ClientHandler handler = new ClientHandler(clientSockets.get(i), i + 1);
				Thread thread = new Thread(handler);
				threads.add(thread);
				thread.start();
			}

			/**
			 * 위 반복문에서 handler.start() 를 호출하면 ClientHandler 의 run() 메소드가 실행.
			 * 이후 handler Thread 들이 모두 종료되면 logResultsForRound() 메소드를 호출하여 결과를 로그파일에 기록.
			 * 이 과정을 ROUND_COUNT 만큼 반복.
			 */

			for (Thread thread : threads) {
				try {
					thread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					e.printStackTrace();
				}
			}
			logResultsForRound(round);
		}
		shutdownServer();
	}

	private void logResultsForRound(int round) {
		// 각 라운드의 결과를 로그 파일에 기록하는 로직을 구현합니다.
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
			log("[RESULT] Round " + (round) + " matrix results");
			out.println();

			Map<Set<String>, int[][]> result = results.get(round - 1);

			for (Set<String> key : result.keySet()) {
				log("[" + key.toString() + "] matrix:");
				int[][] matrix = result.get(key);
				for (int i = 0; i < MATRIX_SIZE; i++) {
					StringBuilder sb = new StringBuilder();
					for (int j = 0; j < MATRIX_SIZE; j++) {
						sb.append(matrix[i][j]).append(" ");
					}
					log(sb.toString());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void log(String message) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
			out.println("[" + SYSTEM_CLOCK.get() + "] " + message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void shutdownServer() {
		try {
			serverSocket.close();
			pool.shutdown();
			log("Server shut down.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class ClientHandler implements Runnable {
		private final Socket clientSocket;
		private final int clientId;

		public ClientHandler(Socket clientSocket, int clientId) {
			this.clientSocket = clientSocket;
			this.clientId = clientId;
		}

		@Override
		public void run() {
			try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
				 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
				// 클라이언트에게 라운드 시작 알리기
				out.println("[ALERT] ROUND START");
				int count = 0;

				while (true) {
					// 각 클라이언트에게 연산 결과를 받고 결과 행렬에 저장.
					String[] inputs = in.readLine().split(" ");
					if (inputs[0].equals("[END]")) break;
					parseAndSave(inputs);
				}
				// 클라이언트에게 라운드 종료 알리기
				out.println("[ALERT] ROUND COMPLETE");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static void parseAndSave(String[] inputs) {
		SYSTEM_CLOCK.incrementAndGet();
		// inputs : [CALC] keys:(1,3) index:(1,2) result:15
		String[] keys = inputs[1].substring(6, inputs[1].length() - 1).split(",");
		String[] indexes = inputs[2].substring(7, inputs[2].length() - 1).split(",");
		int result = Integer.parseInt(inputs[3].substring(7));

		save(keys, indexes, result);
	}

	private static void save(String[] keys, String[] indexes, int result) {
		// HashMap 객체에 결과 대입
		results.get(CURRENT_ROUND.get()) // Map
			.get(new HashSet<String>(Set.of(keys))) // Set
			[Integer.parseInt(indexes[0])][Integer.parseInt(indexes[1])] = result; // int[][]

	}
}