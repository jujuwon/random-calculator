package org.example.handler;

import org.example.vo.ReqFileChunk;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static org.example.Client.MAX_CHUNK_COUNT;
import static org.example.Client.gson;

public class ServerSender implements Runnable {
    private final Socket socket;
    private final int fileId;

    public ServerSender(Socket socket, int fileId) {
        this.socket = socket;
        this.fileId = fileId;
    }

    @Override
    public void run() {
        for(int i = 0; i < MAX_CHUNK_COUNT; i++){
            ReqFileChunk req = new ReqFileChunk(fileId, i);
            request("[REQ] " + gson.toJson(req));
        }
    }

    public synchronized void request(String message) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
