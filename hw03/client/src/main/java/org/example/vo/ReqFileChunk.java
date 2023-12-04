package org.example.vo;

// 클라 간 파일 번호와 청크 내용 요청 / 수신해서 ResFileChunk에 사용
public class ReqFileChunk {
    private int fileId;
    private int chunkIndex;

    public ReqFileChunk(int fileId, int chunkIndex) {
        this.fileId = fileId;
        this.chunkIndex = chunkIndex;
    }

    public int getFileId() {
        return fileId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }
}
