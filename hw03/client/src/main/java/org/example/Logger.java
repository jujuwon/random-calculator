package org.example;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import static org.example.Client.LOG_FILE;

public class Logger {
    public static synchronized void log(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String now = getCurrentTime();
            out.printf("[%s] %s\n", now, message);
            System.out.printf("[%s] %s\n", now, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getCurrentTime() {
        return new SimpleDateFormat("HH:mm:ss:SSS").format(System.currentTimeMillis());
    }
}
