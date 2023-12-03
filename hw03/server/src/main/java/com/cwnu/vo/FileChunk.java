package com.cwnu.vo;

import java.util.List;

public class FileChunk {
	// 파일 번호
	private Integer file_id;
	// 보유한 청크 목록
	private List<Integer> chunks;

	public Integer getFile_id() {
		return file_id;
	}

	public List<Integer> getChunks() {
		return chunks;
	}
}
