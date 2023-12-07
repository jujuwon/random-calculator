package org.example;

import com.google.gson.Gson;
import org.example.handler.ClientReceiver;
import org.example.handler.ServerReceiver;
import org.example.handler.ServerSender;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private static final ExecutorService requesterPool = Executors.newFixedThreadPool(MAX_CLIENT_COUNT);
    private static final ExecutorService receiverPool = Executors.newFixedThreadPool(MAX_CLIENT_COUNT);

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
        Logger.log("Socket connected to Server");

        if(clientId.equals("1")) {
            sockets[2] = socketBinding(8012);
            sockets[3] = socketBinding(8013);
            sockets[4] = socketBinding(8014);
        }
        else if(clientId.equals("2")) {
            sockets[1] = new Socket(SERVERHOST, 8012);
            Logger.log("Client 1 - 2 have socket connections");
            sockets[3] = socketBinding(8023);
            sockets[4] = socketBinding(8024);
        }
        else if(clientId.equals("3")) {
            sockets[1] = new Socket(SERVERHOST, 8013);
            Logger.log("Client 1 - 3 have socket connections");
            sockets[2] = new Socket(SERVERHOST, 8023);
            Logger.log("Client 2 - 3 have socket connections");
            sockets[4] = socketBinding(8034);
        }
        else {
            sockets[1] = new Socket(SERVERHOST, 8014);
            Logger.log("Client 1 - 4 have socket connections");
            sockets[2] = new Socket(SERVERHOST, 8024);
            Logger.log("Client 2 - 4 have socket connections");
            sockets[3] = new Socket(CLIENTHOST, 8034);
            Logger.log("Client 3 - 4 have socket connections");
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

    public void receiving(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            ClientReceiver clientReceiver = new ClientReceiver(sockets[i], clientId);
            receiverPool.execute(clientReceiver);
        }
        ServerReceiver serverReceiver = new ServerReceiver(sockets, clientId);
        serverReceiver.run();
    }

    public void sending(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            ServerSender requester = new ServerSender(sockets[0], i);
            requesterPool.execute(requester);
        }
    }
}
