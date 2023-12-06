package org.example.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSender implements Runnable {
    private final Socket socket;

    public ClientSender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }

    public synchronized void request(String message) {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
