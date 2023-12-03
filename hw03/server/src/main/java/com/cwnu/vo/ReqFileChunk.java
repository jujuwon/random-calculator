package com.cwnu.vo;

/**
 * 클라이언트가 서버에게 파일 청크를 가지고 있는 클라이언트 정보를 요청할 때 사용하는 클래스
 * 예시
 * {
 * 	"file_id": 1,
 * 	"chunk_id": 1
 * }
 */
public class ReqFileChunk {
	private int file_id;
	private int chunk_id;

	public ReqFileChunk(int file_id, int chunk_id) {
		this.file_id = file_id;
		this.chunk_id = chunk_id;
	}

	public int getFile_id() {
		return file_id;
	}

	public int getChunk_id() {
		return chunk_id;
	}
}
