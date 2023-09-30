package com.cwnu;

import java.io.*;
import java.net.*;
import java.util.Random;

public class Client {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static int CLIENT_ID;
    private static String LOG_FILE;

    public static void main(String[] args) {
        CLIENT_ID = (args.length > 0) ? Integer.parseInt(args[0]) : 1;

        writeToLog("Client" + CLIENT_ID + " 연결됨");

        try (Socket socket = new Socket(HOST, PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                String question = in.readLine();
                if (question.equals("TIMEOUT")) {
                    writeToLog("Client" + CLIENT_ID + " 연결종료");
                    break;
                }

                writeToLog("질문 받음: " + question);
                int answer = computeAnswer(question);
                writeToLog("계산 결과: " + answer);

                int next = new Random().nextInt(5);

                out.println(next + " " + answer);
                writeToLog("결과 전송: " + answer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int computeAnswer(String question) {
        String[] parts = question.split(" ");
        int num1 = Integer.parseInt(parts[1]);
        int num2 = Integer.parseInt(parts[3]);
        int num3 = Integer.parseInt(parts[5]);
        char operator1 = parts[2].charAt(0);
        char operator2 = parts[4].charAt(0);

        if (operator1 == '*') {
            num1 *= num2;
            switch (operator2) {
                case '+':
                    return num1 + num3;
                case '-':
                    return num1 - num3;
                case '*':
                    return num1 * num3;
                case '/':
                    return (num3 != 0) ? num1 / num3 : -1;
                default:
                    return -1;
            }
        } else if (operator1 == '/') {
            if (num2 == 0)
                return -1;
            num1 /= num2;
            switch (operator2) {
                case '+':
                    return num1 + num3;
                case '-':
                    return num1 - num3;
                case '*':
                    return num1 * num3;
                case '/':
                    return (num3 != 0) ? num1 / num3 : -1;
                default:
                    return -1;
            }
        } else if (operator1 == '+') {
            switch (operator2) {
                case '+':
                    return num1 + num2 + num3;
                case '-':
                    return num1 + num2 - num3;
                case '*':
                    return num1 + num2 * num3;
                case '/':
                    return (num3 != 0) ? num1 + num2 / num3 : -1;
                default:
                    return -1;
            }
        } else if (operator1 == '-') {
            switch (operator2) {
                case '+':
                    return num1 - num2 + num3;
                case '-':
                    return num1 - num2 - num3;
                case '*':
                    return num1 - num2 * num3;
                case '/':
                    return (num3 != 0) ? num1 - num2 / num3 : -1;
                default:
                    return -1;
            }
        } else
            return -1;
    }

    private static synchronized void writeToLog(String message) {
        LOG_FILE = "Client" + CLIENT_ID + ".txt";
		try (FileWriter fw = new FileWriter(LOG_FILE, true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter out = new PrintWriter(bw)) {
			System.out.println(message);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
