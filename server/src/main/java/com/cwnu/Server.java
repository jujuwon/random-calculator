package com.cwnu;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
	private static final AtomicInteger SYSTEM_CLOCK = new AtomicInteger(0);
	private static final AtomicInteger CONNECTED_SOCKET_COUNT = new AtomicInteger(0);
	private static int TOTAL_SUM = 0;
	private static final int MAX_CONNECT_COUNT = 4;
	private static final int MAX_TIME = 600;
	private static ServerSocket serverSocket;
	private static final List<ClientHandler> handlers = new ArrayList<>();
	private static final String LOG_FILE = "../log/Server.txt";
	private static final Random rand = new Random();

	public static void main(String[] args) throws IOException {
		Server.start();
	}

	public static void start() throws IOException {
		serverSocket = new ServerSocket(8080);

		while (CONNECTED_SOCKET_COUNT.intValue() < MAX_CONNECT_COUNT) {
			Socket clientSocket = serverSocket.accept();
			CONNECTED_SOCKET_COUNT.incrementAndGet();
			handlers.add(new ClientHandler(clientSocket));
		}

		// Start system clock thread
		new Thread(() -> {
			while (SYSTEM_CLOCK.get() < MAX_TIME) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			closeServer();
		}).start();

		for (ClientHandler handler : handlers) {
			handler.start();
		}
	}

	private static void closeServer() {
		try {
			writeToLog("Total Sum : " + TOTAL_SUM + ", Server Shutdown.");
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void addToTotalSum(int value) {
		TOTAL_SUM += value;
	}

	private static synchronized void sendNewQuestion(PrintWriter out, String question, int clientNum) {
		addTimeAndLogging(rand.nextInt(5) + 1, "Question Sent to Client" + clientNum + " : \"" + question + "\"");
		out.printf("[%02d:%02d] %s\n", SYSTEM_CLOCK.get() / 60, SYSTEM_CLOCK.get() % 60, question);
	}

	private static synchronized void sendOriginQuestion(PrintWriter out, String question, int clientNum) {
		addTimeAndLogging(0, "Question \"" + question + "\" is Sent Back to Client" + clientNum);
		out.printf("[%02d:%02d] %s\n", SYSTEM_CLOCK.get() / 60, SYSTEM_CLOCK.get() % 60, question);
	}

	private static synchronized void addTimeAndLogging(int time, String message) {
		SYSTEM_CLOCK.addAndGet(time);
		if (message != null) {
			writeToLog(message);
		}
	}

	private static synchronized void writeToLog(String message) {
		try (FileWriter fw = new FileWriter(LOG_FILE, true);
			 BufferedWriter bw = new BufferedWriter(fw);
			 PrintWriter out = new PrintWriter(bw)) {
			int min = SYSTEM_CLOCK.get() / 60, sec = SYSTEM_CLOCK.get() % 60;
			out.printf("[%02d:%02d] %s\n", min, sec, message);
			System.out.printf("[%02d:%02d] %s\n", min, sec, message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class ClientHandler extends Thread {
		private final Socket socket;
		private final int num;

		public ClientHandler(Socket socket) {
			this.socket = socket;
			this.num = CONNECTED_SOCKET_COUNT.get();
			writeToLog("New Socket Connected. Current Number of Connected Sockets : " + CONNECTED_SOCKET_COUNT.get());
		}

		@Override
		public void run() {
			try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
				String question = generateMathQuestion();
				out.printf("[%02d:%02d] %s\n", SYSTEM_CLOCK.get() / 60, SYSTEM_CLOCK.get() % 60, question);
				writeToLog("Question Sent to Client" + num + " : \"" + question + "\"");

				while (SYSTEM_CLOCK.get() < MAX_TIME) {
					String[] parts = in.readLine().split(" ");

					int time = Integer.parseInt(parts[0].trim());
					int answer = Integer.parseInt(parts[1]);

					if (checkAnswer(question, answer)) {
						addToTotalSum(answer);
						addTimeAndLogging(time, "Client" + num + " Correct Answer : " + answer + ", calculation time : " + time);
						question = generateMathQuestion();
						sendNewQuestion(out, question, num);
					} else {
						addTimeAndLogging(time, "Client" + num + " Incorrect Answer : " + answer + ", calculation time : " + time);
						sendOriginQuestion(out, question, num);
					}
				}
				out.printf("TIMEOUT [%02d:%02d]\n", SYSTEM_CLOCK.get() / 60, SYSTEM_CLOCK.get() % 60);
				writeToLog("Client" + num + " Connection Terminated.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private String generateMathQuestion() {
			Random rand = new Random();
			int num1 = rand.nextInt(101);
			int num2 = rand.nextInt(101);
			int num3 = rand.nextInt(101);
			char operator1 = "+-*/".charAt(rand.nextInt(4));
			char operator2 = "+-*/".charAt(rand.nextInt(4));

			return num1 + " " + operator1 + " " + num2 + " " + operator2 + " " + num3 + " = ?";
		}

		private boolean checkAnswer(String question, int answer) {
			String[] parts = question.split(" ");
			int num1 = Integer.parseInt(parts[0]);
			int num2 = Integer.parseInt(parts[2]);
			int num3 = Integer.parseInt(parts[4]);
			char operator1 = parts[1].charAt(0);
			char operator2 = parts[3].charAt(0);

			if (operator1 == '*') {
				num1 *= num2;
				switch (operator2) {
					case '+':
						return num1 + num3 == answer;
					case '-':
						return num1 - num3 == answer;
					case '*':
						return num1 * num3 == answer;
					case '/':
						if (num3 == 0 && answer == -1)
							return true;
						return num3 != 0 && num1 / num3 == answer;
					default:
						return false;
				}
			} else if (operator1 == '/') {
				if (num2 == 0) {
					if (answer == -1)
						return true;
					return false;
				}
				num1 /= num2;
				switch (operator2) {
					case '+':
						return num1 + num3 == answer;
					case '-':
						return num1 - num3 == answer;
					case '*':
						return num1 * num3 == answer;
					case '/':
						if (num3 == 0 && answer == -1)
							return true;
						return num3 != 0 && num1 / num3 == answer;
					default:
						return false;
				}
			} else if (operator1 == '+') {
				switch (operator2) {
					case '+':
						return num1 + num2 + num3 == answer;
					case '-':
						return num1 + num2 - num3 == answer;
					case '*':
						return num1 + num2 * num3 == answer;
					case '/':
						if (num3 == 0 && answer == -1)
							return true;
						return num3 != 0 && num1 + num2 / num3 == answer;
					default:
						return false;
				}
			} else if (operator1 == '-') {
				switch (operator2) {
					case '+':
						return num1 - num2 + num3 == answer;
					case '-':
						return num1 - num2 - num3 == answer;
					case '*':
						return num1 - num2 * num3 == answer;
					case '/':
						if (num3 == 0 && answer == -1)
							return true;
						return num3 != 0 && num1 - num2 / num3 == answer;
					default:
						return false;
				}
			} else
				return false;
		}
	}
}