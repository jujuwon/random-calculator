package com.cwnu;

import com.cwnu.handler.ClientHandler;
import com.cwnu.vo.ReqFileChunk;
import com.cwnu.vo.ResClientInfo;

public class FileManager {
	// 핸들러들 돌면서 파일 소유 정보 조회
	private final ClientHandler[] clientHandlers;

	public FileManager(ClientHandler[] clientHandlers) {
		this.clientHandlers = clientHandlers;
	}

	public ResClientInfo search(int clientId, ReqFileChunk req) {
		int fileId = req.getFile_id();
		int chunkId = req.getChunk_id();

		if (fileId <= 2) {
			// 만약 파일이 1,2 이면 1,2 클라에서 먼저 찾기
			for (int i = 1; i <= 2; i++) {
				if (i == clientId)
					continue;
				if (clientHandlers[i].search(fileId, chunkId)) {
					return new ResClientInfo(i, clientHandlers[i].getAddress(), req);
				}
			}
			for (int i = 3; i <= 4; i++) {
				if (i == clientId)
					continue;
				if (clientHandlers[i].search(fileId, chunkId)) {
					return new ResClientInfo(i, clientHandlers[i].getAddress(), req);
				}
			}
		} else {
			// 만약 파일이 3,4 이면 3,4 클라에서 먼저 찾기
			for (int i = 3; i <= 4; i++) {
				if (i == clientId)
					continue;
				if (clientHandlers[i].search(fileId, chunkId)) {
					return new ResClientInfo(i, clientHandlers[i].getAddress(), req);
				}
			}
			for (int i = 1; i <= 2; i++) {
				if (i == clientId)
					continue;
				if (clientHandlers[i].search(fileId, chunkId)) {
					return new ResClientInfo(i, clientHandlers[i].getAddress(), req);
				}
			}
		}
		Logger.log("Retained file chunk retrieval error");
		System.out.println("Retained file chunk retrieval error");
		throw new RuntimeException();
	}

	public boolean checkIfAllFilesAreReceived() {
		for (ClientHandler handler : clientHandlers) {
			if (!handler.isAllFilesReceived()) {
				return false;
			}
		}
		Logger.log("All files received");
		return true;
	}
}
