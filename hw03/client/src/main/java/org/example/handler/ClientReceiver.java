package org.example.handler;

import org.example.Logger;
import org.example.vo.ReqFileChunk;
import org.example.vo.ResFileChunk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.Socket;

import static org.example.Client.*;

public class ClientReceiver implements Runnable {
    private final Socket socket;
    private final String clientId;

    public ClientReceiver(Socket socket, String clientId) {
        this.socket = socket;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = in.readLine();
            String[] split = message.split(" ");
            String type = split[0];

            if("[CHUNKREQ]".equals(type)) {
                ReqFileChunk req = gson.fromJson(parse(split), ReqFileChunk.class);
                int fileId = req.getFileId();
                int index = req.getChunkIndex();
                String chunk = fileReading(fileId, index);
                ResFileChunk res = new ResFileChunk(req, chunk);
                ClientSender sender = new ClientSender(socket);
                Logger.log("(CHUNKINFOREC) Chunk " + index + " of file " + fileId);
                sender.request("[CHUNKRES] " + gson.toJson(res));
                Logger.log("(CHUNKSEND) Chunk " + index + " of file " + fileId);
            }
            else {
                ResFileChunk res = gson.fromJson(parse(split), ResFileChunk.class);
                ReqFileChunk req = res.getReqFileChunk();
                int fileId = req.getFileId();
                int index = req.getChunkIndex();
                fileWriting(fileId, res.getChunk(), index);
                Logger.log("(CHUNKREC) Chunk " + index + " of file " + fileId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parse(String[] split) {
        StringBuilder jsonData = new StringBuilder();
        for(int i = 1; i < split.length; i++) {
            jsonData.append(split[i]);
        }
        return jsonData.toString();
    }

    public synchronized String fileReading(int filename, int index) throws IOException {
        byte[] buffer = new byte[CHUNK_SIZE];
        try (RandomAccessFile raf = new RandomAccessFile(FILEDIR + filename + ".file", "r")) {
            raf.seek(index * CHUNK_SIZE);
            raf.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    public synchronized void fileWriting(int filename, String chunk, int index) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILEDIR + filename + ".file", "rw")) {
            raf.seek(index);
            byte[] buf = chunk.getBytes();
            raf.write(buf);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
