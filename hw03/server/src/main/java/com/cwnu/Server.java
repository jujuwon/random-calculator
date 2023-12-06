package com.cwnu;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cwnu.handler.ClientHandler;
import com.cwnu.handler.Receiver;
import com.cwnu.handler.Requester;

public class Server {

	private static ServerSocket serverSocket;
	private static final int PORT = 8080;
	public static final int MAX_CONNECT_COUNT = 4;
	private static final ClientHandler[] clientHandlers = new ClientHandler[MAX_CONNECT_COUNT + 1];
	private static final Requester[] requesters = new Requester[MAX_CONNECT_COUNT + 1];
	private static final Receiver[] receivers = new Receiver[MAX_CONNECT_COUNT + 1];
	private static final ExecutorService requesterPool = Executors.newFixedThreadPool(MAX_CONNECT_COUNT);
	private static final ExecutorService receiverPool = Executors.newFixedThreadPool(MAX_CONNECT_COUNT);
	public static final int FILE_COUNT = 4;
	private FileManager fileManager;
	private ShutdownServerTask shutdownServerTask;

	public Server() throws IOException {
		serverSocket = new ServerSocket(PORT);
	}

	public void start() throws IOException {
		// 1) 각 클라이언트랑 소켓 연결 맺기
		// 파일 정보 조회용 객체 생성
		init();

		// 2) 각 클라이언트에게 주기적으로 보유 파일 정보 요청
		polling();

		// 3) 클라이언트로부터 특정 파일, 청크의 요청을 받으면
		// Map 에서 해당 파일, 청크를 보여하고 있는 클라이언트 정보 리턴
		receive();

		// 4) 서버 종료
		shutdown();
	}

	/**
	 * 1)
	 * 각 클라이언트랑 소켓 연결 맺기
	 * 파일 정보 조회용 객체 생성
	 */
	private void init() throws IOException {
		for (int clientId = 1; clientId <= MAX_CONNECT_COUNT; clientId++) {
			Socket socket = serverSocket.accept();
			clientHandlers[clientId] = new ClientHandler(clientId, socket);
		}
		fileManager = new FileManager(clientHandlers);
		shutdownServerTask = new ShutdownServerTask(fileManager, requesters, receivers, requesterPool, receiverPool);
	}

	/**
	 * 2)
	 * 각 클라이언트에게 주기적으로 보유 파일 정보 요청
	 */
	private void polling() {
		for (int clientId = 1; clientId <= MAX_CONNECT_COUNT; clientId++) {
			requesters[clientId] = new Requester(clientHandlers[clientId]);
			requesterPool.execute(requesters[clientId]);
		}
	}

	/**
	 * 3)
	 * 클라이언트로부터 특정 파일, 청크의 요청을 받으면
	 * Map 에서 해당 파일, 청크를 보여하고 있는 클라이언트 정보를 리턴함.
	 */
	private void receive() {
		for (int clientId = 1; clientId <= MAX_CONNECT_COUNT; clientId++) {
			receivers[clientId] = new Receiver(clientHandlers[clientId], fileManager);
			receiverPool.execute(receivers[clientId]);
		}
	}

	/**
	 * 4)
	 * 서버 종료
	 */
	private void shutdown() {
		new Thread(shutdownServerTask).start();
	}
}
