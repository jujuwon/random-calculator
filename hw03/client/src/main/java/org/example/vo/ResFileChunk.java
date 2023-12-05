package org.example.vo;

// 파일 청크 내용 담아서 송 / 수신
public class ResFileChunk {
    private ReqFileChunk reqFileChunk;
    private String chunk;

    public ResFileChunk(ReqFileChunk reqFileChunk, String chunk) {
        this.reqFileChunk = reqFileChunk;
        this.chunk = chunk;
    }

    public ReqFileChunk getReqFileChunk() {
        return reqFileChunk;
    }

    public String getChunk() {
        return chunk;
    }
}
