package org.example;

import java.net.*;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;

public class client {
    private static final int PORT = 8000;
    private static final String SERVERHOST = "127.0.0.1";
    private static final String CLIENTHOST = "127.0.0.1";
    private static ServerSocket serverSocket1, serverSocket2, serverSocket3;
    private static Socket clientSocket, socket1, socket2, socket3;
    private static String LOG_FILE = "../log/client";
    private static String FILEDIR = "../file/client";
    private static final int CHUNK_SIZE = 1024 * 256;
    private static byte[] buffer = new byte[CHUNK_SIZE];
    private static String chunk;
    // CHUNK_COUNT = 1954 (0 ~ 1953);
    private static Map<Integer, boolean[]> chunks = new HashMap<>();
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        fileDirectoring(args[0]);


        chunk = fileReading(0);
        System.out.println(chunk);

        fileWriting("2", "1234", 50);
        fileWriting("2", "5678", 10);
    }

    private static void init(String clientId) throws IOException {
        fileDirectoring(clientId);

        socketConnection(clientId);


    }

    private static void fileDirectoring(String clientId) {
        FILEDIR += clientId + "/";
        LOG_FILE += clientId + ".txt";
    }

    private static void socketConnection(String clientId) throws IOException {
        if(clientId.equals("1")) {
            socketConnecting(clientSocket, SERVERHOST, PORT);
            socketBinding(serverSocket1, 8012);
            socketBinding(serverSocket2, 8013);
            socketBinding(serverSocket3, 8014);
        }
        else if(clientId.equals("2")) {
            socketConnecting(clientSocket, SERVERHOST, PORT);
            socketConnecting(socket1, CLIENTHOST, 8012);
            socketBinding(serverSocket1, 8023);
            socketBinding(serverSocket2, 8024);
        }
        else if(clientId.equals("3")) {
            socketConnecting(clientSocket, SERVERHOST, PORT);
            socketConnecting(socket1, CLIENTHOST, 8013);
            socketConnecting(socket2, CLIENTHOST, 8023);
            socketBinding(serverSocket1, 8034);
        }
        else {
            socketConnecting(clientSocket, SERVERHOST, PORT);
            socketConnecting(socket1, CLIENTHOST, 8014);
            socketConnecting(socket2, CLIENTHOST, 8024);
            socketConnecting(socket3, CLIENTHOST, 8034);
        }
    }

    private static void closeSocket(String clientId) {
        if(clientId.equals("1")) {
            try {
                serverSocket1.close();
                serverSocket2.close();
                serverSocket3.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(clientId.equals("2")) {
            try {
                serverSocket1.close();
                serverSocket2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(clientId.equals("3")) {
            try {
                serverSocket1.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void socketConnecting(Socket socket, String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    private static void socketBinding(ServerSocket serverSocket, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.accept();
    }

    private static String fileReading(int index) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILEDIR + "1.file", "r")) {
            raf.seek(index * CHUNK_SIZE);
            raf.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    private static void fileWriting(String filename, String str, int index) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILEDIR + filename + ".file", "rw")) {
            raf.seek(index);
            byte[] buf = str.getBytes();
            raf.write(buf);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void log(String message) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.println(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerReqReceiver implements Runnable {
        private final Socket socket;
        private final int clientId;

        public ServerReqReceiver(Socket socket, int clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {

        }
    }


    private class ServerResSender implements Runnable {


        @Override
        public void run() {

        }
    }

    private class ChunkRequester implements Runnable {


        @Override
        public void run() {

        }
    }

    private class ChunkInfoSender implements Runnable {


        @Override
        public void run() {

        }
    }
}
