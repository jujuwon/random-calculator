package org.example.handler;

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

    public ClientReceiver(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String message = in.readLine();
            String[] split = message.split(" ");
            String type = split[0];

            if("[CHUNKREQ]".equals(type)) {
                ReqFileChunk req = gson.fromJson(parse(split), ReqFileChunk.class);
                String chunk = fileReading(req.getFileId(), req.getChunkIndex());
                ResFileChunk res = new ResFileChunk(req, chunk);
                ClientSender sender = new ClientSender(socket);
                sender.request("[CHUNKRES] " + gson.toJson(res));
            }
            else {
                ResFileChunk res = gson.fromJson(parse(split), ResFileChunk.class);
                ReqFileChunk req = res.getReqFileChunk();
                int fileId = req.getFileId();
                int index = req.getChunkIndex();
                fileWriting(fileId, res.getChunk(), index);
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
