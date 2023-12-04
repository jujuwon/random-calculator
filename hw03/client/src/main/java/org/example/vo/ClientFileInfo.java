package org.example.vo;

import java.util.List;

// 클라 단위로 가지고 있는 파일 청크
public class ClientFileInfo {
    private List<FileChunk> fileChunks;

    public ClientFileInfo(List<FileChunk> fileChunks) {
        this.fileChunks = fileChunks;
    }
}
