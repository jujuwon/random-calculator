package org.example.handler;

import org.example.vo.ClientFileInfo;
import org.example.vo.FileChunk;
import org.example.vo.ReqFileChunk;
import org.example.vo.ResClientInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;
import static org.example.Client.*;

public class ServerReceiver implements Runnable {
    private final Socket[] sockets;
    private final String clientId;

    public ServerReceiver(Socket[] sockets, String clientId) {
        this.sockets = sockets;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(sockets[0].getInputStream()))) {

            System.out.println("test");
            exit(1);

            String message = in.readLine();
            String[] split = message.split(" ");
            String type = split[0];

            if("[REQ]".equals(type)) {
                List<FileChunk> fileInfo = new ArrayList<>();
                for(int i = 1; i <= MAX_FILE_COUNT; i++) {
                    fileInfo.add(new FileChunk(i, chunks[i]));
                    ClientFileInfo clientFileInfo = new ClientFileInfo(fileInfo);
                    ServerSender responser = new ServerSender(sockets[0], Integer.parseInt(clientId));
                    responser.request("[RES] " + gson.toJson(clientFileInfo));
                }
            }
            else if("[END]".equals(type)) {
                closeSocket();
            }
            else {
                ResClientInfo res = gson.fromJson(parse(split), ResClientInfo.class);
                int clientId = res.getClientId();
                ReqFileChunk req = res.getReqFileChunk();
                for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
                    if(clientId == i) {
                        ClientSender requester = new ClientSender(sockets[i]);
                        requester.request("[CHUNKREQ] " + gson.toJson(req));
                    }
                }
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

    public void closeSocket() {
        try{
            for(ServerSocket serverSocket : serverSockets)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}