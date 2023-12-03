package com.cwnu.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.cwnu.FileManager;
import com.cwnu.vo.ReqFileChunk;
import com.cwnu.vo.ResClientFileInfo;
import com.cwnu.vo.ResClientInfo;
import com.google.gson.Gson;

public class Receiver implements Runnable {
	private final ClientHandler handler;
	private Requester requester;
	private final FileManager manager;
	private final Gson gson = new Gson();

	public Receiver(ClientHandler handler, FileManager manager) {
		this.handler = handler;
		this.manager = manager;
		this.requester = new Requester(handler);
	}

	@Override
	public void run() {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(handler.getSocket().getInputStream()))) {
			// 클라이언트가 가지고 있는 파일 및 청크 정보 받기
			String receiveMessage = in.readLine();
			String[] split = receiveMessage.split(" ");
			String type = split[0];

			if ("[RES]".equals(type)) {
				// [RES] 파일 소유 정보 응답
				ResClientFileInfo res = gson.fromJson(parse(split), ResClientFileInfo.class);
				handler.updateChunks(res);
			} else {
				// [REQ] 클라이언트 요청
				ReqFileChunk req = gson.fromJson(parse(split), ReqFileChunk.class);
				// 여기서 계산
				ResClientInfo res = manager.search(handler.getClientId(), req);
				requester.request("[RES] " + gson.toJson(res));
			}

		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}

	private String parse(String[] split) {
		StringBuilder jsonData = new StringBuilder();
		for (int i = 1; i < split.length; i++) {
			jsonData.append(split[i]);
		}
		return jsonData.toString();
	}
}
