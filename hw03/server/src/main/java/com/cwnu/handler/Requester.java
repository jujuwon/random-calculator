package com.cwnu.handler;

import java.io.IOException;
import java.io.PrintWriter;

public class Requester implements Runnable {
	private final ClientHandler handler;

	public Requester(ClientHandler handler) {
		this.handler = handler;
	}

	@Override
	public void run() {
		try {
			request("[REQ]");
			// 1초마다 한번씩 클라이언트의 파일 소유 정보 받기
			Thread.sleep(1_000);
		} catch (InterruptedException e) {
			System.out.println("InterruptedException");
			e.printStackTrace();
		}
	}

	public synchronized void request(String message) {
		try (PrintWriter out = new PrintWriter(handler.getSocket().getOutputStream(), true)) {
			out.println(message);
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
}