import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Client1 {
    private static final int PORT = 8000, PORT12 = 8012, PORT13 = 8013, PORT14 = 8014;
    private static ServerSocket serverSocket12, serverSocket13, serverSocket14;
    private static final String LOG_FILE = "../../log/client1.txt";
    private static Socket clientSocket, socket12, socket13, socket14;
    private static PrintWriter out;
    private static BufferedReader in;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static String[] message1, message2;

    public static void main(String[] args) throws IOException {
        socketConnection();
        
        while(round <= 100) {
            if(in.readLine().equals("[ALERT] ROUND START")){
                log("ROUND " + round + " start.");
                setMatrix();
            }
            
            ClientThread client1 = new ClientThread(socket12, 2, socket13, 3);
            ClientThread client2 = new ClientThread(socket12, 2, socket14, 4);
            ClientThread client3 = new ClientThread(socket13, 3, socket14, 4);
            
            Thread thread1 = new Thread(client1);
            Thread thread2 = new Thread(client2);
            Thread thread3 = new Thread(client3);

            thread1.start();
            thread2.start();
            thread3.start();

            try{
                thread1.join();
                thread2.join();
                thread3.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            out.println("[END]");
            log("ROUND " + round + " end.");
            round++;
            in.readLine();
        }
        closeSocket();
    }

    private static void socketConnection() throws IOException {
        clientSocket = new Socket("127.0.0.1", PORT);
        log("Client 1 connected.");

        serverSocket12 = new ServerSocket(PORT12);
        serverSocket13 = new ServerSocket(PORT13);
        serverSocket14 = new ServerSocket(PORT14);
        socket12 = serverSocket12.accept();
        log("Client 1 - 2 connected.");
        socket13 = serverSocket13.accept();
        log("Client 1 - 3 connected.");
        socket14 = serverSocket14.accept();
        log("Client 1 - 4 connected.");

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    private static void setMatrix() {
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++)
                matrix[i][j] = (int)(Math.random() * 100);
        }
    }

    private static synchronized String getClientMessage(int cnt, int mode) {
        String message = "";
        if(mode == 1){
            for(int i = 0; i < 10; i++){
                message += matrix[cnt][i] + " ";
            }
        }
        else {
            for(int i = 0; i < 10; i++) {
                message += matrix[i][cnt] + " ";
            }
        }
        System.out.println(cnt + " " + message);
        return cnt + " " + message;
    }

    private static int calculate(String[] msg1, String[] msg2) {
        int result = 0;
        for(int i = 0; i < 10; i++){
            result += Integer.parseInt(msg1[i + 1]) * Integer.parseInt(msg2[i + 1]);
        }
        return result;
    }

    private static void log(String message) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
			out.println(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

    private static void closeSocket() {
        try {
            serverSocket12.close();
            serverSocket13.close();
            serverSocket14.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ClientThread extends Thread {
        private final Socket clientSocket1, clientSocket2;
        private final int clientId1, clientId2;

        public ClientThread(Socket clientSocket1, int clientId1, Socket clientSocket2, int cliendId2) {
            this.clientSocket1 = clientSocket1;
            this.clientSocket2 = clientSocket2;
            this.clientId1 = clientId1;
            this.clientId2 = cliendId2;
        }

        @Override
        public void run() {
            try (PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(), true);
                BufferedReader in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
                PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream());
                BufferedReader in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()))) {
                
                int count = 0;
                while(count < 100) {

                    // message send function synchronized
                    if(count % 2 == 0)
                        out1.println(getClientMessage(count % 10, 1));
                    else
                        out2.println(getClientMessage(count % 10, 2));
                    count++;
                    
                    // synchronized
                    getMessageAndCalAndSending(in1, in2, clientId1, clientId2);
                }

            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
    }

    private static synchronized void getMessageAndCalAndSending(BufferedReader in1, BufferedReader in2, int id1, int id2) throws IOException {
        message1 = in1.readLine().split(" ");
        message2 = in2.readLine().split(" ");
        int result = calculate(message1, message2);
        
        out.println("[CALC] keys:(" + id1 + "," + id2 + ") index:(" + message1[0] + "," + message2[0] + ") result:" + result);
        log("[CALC] keys:(" + id1 + "," + id2 + ") index:(" + message1[0] + "," + message2[0] + ") result:" + result);
    }
}