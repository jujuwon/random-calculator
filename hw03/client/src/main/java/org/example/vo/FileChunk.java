package org.example.vo;

import java.util.List;

// 파일 단위로 가지고 있는 파일 청크
public class FileChunk {
    private Integer fileId;
    private List<Integer> chunk;

    public FileChunk(Integer fileId, List<Integer> chunk) {
        this.fileId = fileId;
        this.chunk = chunk;
    }
}
