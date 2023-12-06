package org.example;

import com.google.gson.Gson;
import org.example.handler.ClientReceiver;
import org.example.handler.ServerReceiver;
import org.example.handler.ServerSender;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private static final int PORT = 8080;
    private static final String SERVERHOST = "3.34.104.196";
    private static final String CLIENTHOST = "127.0.0.1";
    public static String LOG_FILE = "../log/client";
    public static String FILEDIR = "../file/client";
    public static final int MAX_CLIENT_COUNT = 4;
    public static final int MAX_FILE_COUNT = 4;
    public static final int CHUNK_SIZE = 1024 * 256;
    public static final int MAX_CHUNK_COUNT = 1954;
    public static List<ServerSocket> serverSockets = new ArrayList<>();
    private static Socket[] sockets = new Socket[MAX_CLIENT_COUNT + 1];
    // private static String chunk;
    // CHUNK_COUNT = 1954 (0 ~ 1953);
    public static List<Integer>[] chunks = new ArrayList[MAX_FILE_COUNT + 1];
    public static final Gson gson = new Gson();

    public void start(String clientId) throws IOException {
        init(clientId);

        receiving(clientId);

        sending(clientId);
    }

    public void fileDirectoring(String clientId) {
        FILEDIR += clientId + "/";
        LOG_FILE += clientId + ".txt";
    }

    public Socket socketBinding(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSockets.add(serverSocket);
        return serverSocket.accept();
    }

    public void socketConnection(String clientId) throws IOException {
        sockets[0] = new Socket(SERVERHOST, PORT);
        System.out.println("test");

        if(clientId.equals("1")) {
            sockets[2] = socketBinding(8012);
            sockets[3] = socketBinding(8013);
            sockets[4] = socketBinding(8014);
        }
        else if(clientId.equals("2")) {
            sockets[1] = new Socket(CLIENTHOST, 8012);
            sockets[3] = socketBinding(8023);
            sockets[4] = socketBinding(8024);
        }
        else if(clientId.equals("3")) {
            sockets[1] = new Socket(CLIENTHOST, 8013);
            sockets[2] = new Socket(CLIENTHOST, 8023);
            sockets[4] = socketBinding(8034);
        }
        else {
            sockets[1] = new Socket(CLIENTHOST, 8014);
            sockets[2] = new Socket(CLIENTHOST, 8024);
            sockets[3] = new Socket(CLIENTHOST, 8034);
        }
    }

    public void init(String clientId) throws IOException {
        fileDirectoring(clientId);

        socketConnection(clientId);

        for(int i = 1; i <= MAX_FILE_COUNT; i++) {
            chunks[i] = new ArrayList<>();
            if(Integer.parseInt(clientId) == i) {
                for(int j = 0; j < MAX_CHUNK_COUNT; j++)
                    chunks[i].add(j);
            }
        }
    }

    public void sending(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            new Thread(new ServerSender(sockets[0], i)).start();
        }
    }

    public void receiving(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            // 밑에 쓰레드 2개 while문 걸어서 클라sender로 [END]전송?
            new Thread(new ServerReceiver(sockets, clientId)).start();
            new Thread(new ClientReceiver(sockets[i])).start();
        }
    }


    public synchronized void log(String message) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.println(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
