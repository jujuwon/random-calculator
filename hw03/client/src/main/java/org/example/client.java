package org.example;

import java.net.*;
import java.util.*;
import java.io.*;
import com.google.gson.Gson;
import org.example.vo.ClientFileInfo;
import org.example.vo.FileChunk;
import org.example.vo.ReqFileChunk;
import org.example.vo.ResClientInfo;

public class client {
    private static final int PORT = 8000;
    private static final String SERVERHOST = "127.0.0.1";
    private static final String CLIENTHOST = "127.0.0.1";
    private static List<ServerSocket> serverSockets = new ArrayList<>();
    private static Socket[] sockets = new Socket[5];
    private static String LOG_FILE = "../log/client";
    private static String FILEDIR = "../file/client";
    private static final int MAX_CLIENT_COUNT = 4;
    private static final int MAX_FILE_COUNT = 4;
    private static final int CHUNK_SIZE = 1024 * 256;
    private static final int MAX_CHUNK_COUNT = 1954;
    private static byte[] buffer = new byte[CHUNK_SIZE];
    private static String chunk;
    // CHUNK_COUNT = 1954 (0 ~ 1953);
    private static List<Integer>[] chunks = new ArrayList[5];
    private static final Gson gson = new Gson();

    public static void main(String[] args) throws IOException {
        init(args[0]);

        start(args[0]);

        receiving(args[0]);
        /*
        chunk = fileReading(0);
        System.out.println(chunk);

        fileWriting("2", "1234", 50);
        fileWriting("2", "5678", 10);*/
    }

    private static void fileDirectoring(String clientId) {
        FILEDIR += clientId + "/";
        LOG_FILE += clientId + ".txt";
    }

    private static Socket socketBinding(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        serverSockets.add(serverSocket);
        return serverSocket.accept();
    }

    private static void socketConnection(String clientId) throws IOException {
        sockets[0] = new Socket(SERVERHOST, PORT);
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

    private static void init(String clientId) throws IOException {
        fileDirectoring(clientId);

        // socketConnection(clientId);

        for(int i = 1; i <= MAX_FILE_COUNT; i++) {
            chunks[i] = new ArrayList<>();
            if(Integer.parseInt(clientId) == i) {
                for(int j = 0; j < MAX_CHUNK_COUNT; j++)
                    chunks[i].add(j);
            }
        }
    }

    private static void start(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            new Thread(new ServerSender(sockets[i], i)).start();
        }
    }

    private static void receiving(String clientId) throws IOException {
        for(int i = 1; i <= MAX_CLIENT_COUNT; i++) {
            if(Integer.parseInt(clientId) == i)
                continue;
            new Thread(new ServerReceiver(sockets[i], clientId)).start();
        }
    }

    private static void closeSocket() {
        try{
            for(ServerSocket serverSocket : serverSockets)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized String fileReading(String filename, int index) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(FILEDIR + filename + ".file", "r")) {
            raf.seek(index * CHUNK_SIZE);
            raf.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new String(buffer);
    }

    private static synchronized void fileWriting(String filename, String str, int index) throws IOException {
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

    private static class ServerReceiver implements Runnable {
        private final Socket socket;
        private final String clientId;

        public ServerReceiver(Socket socket, String clientId) {
            this.socket = socket;
            this.clientId = clientId;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message = in.readLine();
                String[] split = message.split(" ");
                String type = split[0];

                if("[REQ]".equals(type)) {
                    List<FileChunk> fileInfo = new ArrayList<>();
                    for(int i = 1; i <= MAX_FILE_COUNT; i++) {
                        fileInfo.add(new FileChunk(i, chunks[i]));
                        ClientFileInfo clientFileInfo = new ClientFileInfo(fileInfo);
                        ServerSender responser = new ServerSender(socket, Integer.parseInt(clientId));
                        responser.request("[RES]" + gson.toJson(clientFileInfo));
                    }
                }
                else if("[END]".equals(type)) {
                    closeSocket();
                }
                else {
                    ResClientInfo res = gson.fromJson(parse(split), ResClientInfo.class);

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
    }


    private static class ServerSender implements Runnable {
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
                request("[REQ]" + gson.toJson(req));
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

    private class ClientReceiver implements Runnable {


        @Override
        public void run() {

        }


    }

    private class ClientSender implements Runnable {


        @Override
        public void run() {

        }
    }
}
