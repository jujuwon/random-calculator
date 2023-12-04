package org.example.vo;

// 파일 청크 내용 담아서 송 / 수신
public class ResFileChunk {
    private int fileId;
    private int chunkIndex;
    private String chunk;

    public ResFileChunk(ReqFileChunk req) {
        this.fileId = req.getFileId();
        this.chunkIndex = req.getChunkIndex();
    }

    public int getFileId() {
        return fileId;
    }

    public int getChunkIndex() {
        return chunkIndex;
    }

    public String getChunk() {
        return chunk;
    }
}
