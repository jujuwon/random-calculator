package com.cwnu;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	private static final AtomicInteger SYSTEM_CLOCK = new AtomicInteger(0);
	private static final AtomicInteger CONNECTED_SOCKET_COUNT = new AtomicInteger(1);
	private static int TOTAL_SUM = 0;
	private static final int MAX_CONNECT_COUNT = 4;
	private static final int MAX_TIME = 600;
	private static ServerSocket serverSocket;
	private static final List<ClientHandler> handlers = new ArrayList<>();
	private static final String LOG_FILE = "Server.txt";

	public static void start() throws IOException {
		serverSocket = new ServerSocket(8080);

		while (CONNECTED_SOCKET_COUNT.intValue() <= MAX_CONNECT_COUNT) {
			Socket clientSocket = serverSocket.accept();
			handlers.add(new ClientHandler(clientSocket));
			CONNECTED_SOCKET_COUNT.incrementAndGet();
		}

		// Start system clock thread
		new Thread(() -> {
			while (SYSTEM_CLOCK.get() < MAX_TIME) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			closeServer();
		}).start();

		for (ClientHandler handler : handlers) {
			handler.start();
		}
	}

	private static void closeServer() {
		try {
			writeToLog("Server 종료. Total Sum: " + TOTAL_SUM);
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void addToTotalSum(int value) {
		TOTAL_SUM += value;
	}

	private static synchronized void writeToLog(String message) {
		try (FileWriter fw = new FileWriter(LOG_FILE, true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter out = new PrintWriter(bw)) {
			System.out.println("[" + SYSTEM_CLOCK.get() / 60 + ":" + SYSTEM_CLOCK.get() % 60 + "] " + message);
			out.println("[" + SYSTEM_CLOCK.get() / 60 + ":" + SYSTEM_CLOCK.get() % 60 + "] " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ClientHandler extends Thread {
		private final Socket socket;
		private final int num;

		public ClientHandler(Socket socket) {
			this.socket = socket;
			this.num = CONNECTED_SOCKET_COUNT.get();
			writeToLog("새 Socket 연결. 현재 연결 Socket 개수 : " + CONNECTED_SOCKET_COUNT.get());
		}

		@Override
		public void run() {
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

				while (SYSTEM_CLOCK.get() < MAX_TIME) {
					String question = generateMathQuestion();
					writeToLog("Client" + num + "에게 보낸 질문: \"" + question + "\"");
					out.println(question);

					String answerStr = in.readLine();
					String[] parts = answerStr.split(" ");
					int time = Integer.parseInt(parts[0]);
					int answer = Integer.parseInt(parts[1]);
					if (checkAnswer(question, answer)) {
						addToTotalSum(answer);
						writeToLog("Client" + num + " 정답: " + answer);
						SYSTEM_CLOCK.addAndGet(time);
					} else {
						writeToLog("Client" + num + " 오답: " + answer);
					}
				}
				out.println("TIMEOUT");
				writeToLog("Client" + num + " 연결종료");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String generateMathQuestion() {
			Random rand = new Random();
			int num1 = rand.nextInt(101);
			int num2 = rand.nextInt(101);
			char operator = "+-*/".charAt(rand.nextInt(4));

			return "[" + SYSTEM_CLOCK + "] " + num1 + " " + operator + " " + num2 + " = ";
		}

		private boolean checkAnswer(String question, int answer) {
			String[] parts = question.split(" ");
			int num1 = Integer.parseInt(parts[1]);
			int num2 = Integer.parseInt(parts[3]);
			char operator = parts[2].charAt(0);

			switch (operator) {
				case '+':
					return num1 + num2 == answer;
				case '-':
					return num1 - num2 == answer;
				case '*':
					return num1 * num2 == answer;
				case '/':
					return num2 != 0 && num1 / num2 == answer;
				default:
					return false;
			}
		}
	}
}
