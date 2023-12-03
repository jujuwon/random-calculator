package com.cwnu.handler;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cwnu.Logger;
import com.cwnu.Server;
import com.cwnu.vo.ResClientFileInfo;
import com.cwnu.vo.FileChunk;

public class ClientHandler extends Thread {

	private final int clientId;
	private final Socket socket;
	private final InetAddress address;
	// Key: 파일 식별자, Value: 보유중인 청크 목록
	private Map<Integer, Boolean[]> chunks = new HashMap<>();

	public int getClientId() {
		return clientId;
	}

	public Socket getSocket() {
		return socket;
	}

	public InetAddress getAddress() {
		return address;
	}

	public ClientHandler(int clientId, Socket socket) {
		this.clientId = clientId;
		this.socket = socket;
		this.address = socket.getInetAddress();
		initChunks();
		log();
	}

	private void initChunks() {
		// 파일은 1,2,3,4
		// 현재 id 에 해당하는 파일을 가지고 있다고 가정.
		this.chunks = new HashMap<>();
		for (int i = 0; i < Server.FILE_COUNT; i++) {
			Boolean[] chunk = new Boolean[1954];
			if (clientId == i + 1) {
				Arrays.fill(chunk, true);
			} else {
				Arrays.fill(chunk, false);
			}
			chunks.put(i + 1, chunk);
		}
	}

	public void updateChunks(ResClientFileInfo info) {
		List<FileChunk> fileChunks = info.getFile_chunks();

		for (FileChunk fileChunk : fileChunks) {
			Integer fileId = fileChunk.getFile_id();
			fileChunk.getChunks().forEach(chunkId -> {
				if (!chunks.get(fileId)[chunkId]) {
					chunks.get(fileId)[chunkId] = true;
					Logger.log("Client " + clientId + " has chunk " + chunkId + " of file " + fileId);
				}
			});
		}
	}

	public boolean search(int fileId, int chunkId) {
		return chunks.get(fileId)[chunkId];
	}

	public boolean isAllFilesReceived() {
		for (Boolean[] chunk : chunks.values()) {
			for (Boolean b : chunk) {
				if (!b) {
					return false;
				}
			}
		}
		return true;
	}

	private void log() {
		Logger.log("Client " + clientId + " connected from " + address);
	}
}
