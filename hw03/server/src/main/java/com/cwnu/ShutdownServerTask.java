package com.cwnu;

import static com.cwnu.Server.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import com.cwnu.handler.Receiver;
import com.cwnu.handler.Requester;

public class ShutdownServerTask implements Runnable {
	private final FileManager fileManager;
	private final Requester[] requesters;
	private final Receiver[] receivers;
	private final ExecutorService requesterPool;
	private final ExecutorService receiverPool;

	public ShutdownServerTask(FileManager fileManager, Requester[] requesters, Receiver[] receivers,
		ExecutorService requesterPool, ExecutorService receiverPool) {
		this.fileManager = fileManager;
		this.requesters = requesters;
		this.receivers = receivers;
		this.requesterPool = requesterPool;
		this.receiverPool = receiverPool;
	}

	/**
	 * 서버 종료
	 * 종료 조건 : 모든 클라이언트가 모든 파일을 가지고 있을 때
	 */
	@Override
	public void run() {
		// 모든 클라이언트가 모든 파일을 가지고 있을 때 종료
		while (!fileManager.checkIfAllFilesAreReceived()) {
			try {
				Thread.sleep(1_000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}

		// 모든 클라이언트에게 종료 메시지 전송
		for (int clientId = 1; clientId <= MAX_CONNECT_COUNT; clientId++) {
			requesters[clientId].request("[END]");
		}

		requesterPool.shutdown();
		receiverPool.shutdown();

		// 스레드 풀 종료 대기
		try {
			requesterPool.awaitTermination(10, TimeUnit.SECONDS);
			receiverPool.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
	}
}