package com.cwnu.vo;

import java.util.List;

/**
 * 클라이언트가 서버에게 현재 본인이 소유중인 청크 정보를 응답할 때 사용하는 클래스
 * 예시
 * {
 * 	"file_chunks": [
 *        {
 * 			"file_id": 1,
 * 			"chunks": [1, 2, 3, 4, 5]
 *        },
 *        {
 * 			"file_id": 2,
 * 			"chunks": []
 *        },
 *        {
 * 			"file_id": 3,
 * 			"chunks": [1, 2, 3, 4, 5]
 *        },
 *        {
 * 			"file_id": 4,
 * 			"chunks": [1, 2, 3, 4, 5]
 *        }
 * 	]
 * }
 */
public class ResClientFileInfo {
	private List<FileChunk> file_chunks;

	public List<FileChunk> getFile_chunks() {
		return file_chunks;
	}
}

