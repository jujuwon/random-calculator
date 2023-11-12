import java.net.*;
import java.util.*;
import java.io.*;

public class Client1 {
    private static final int PORT = 8000, PORT12 = 8012, PORT13 = 8013, PORT14 = 8014;
    private static ServerSocket serverSocket12, serverSocket13, serverSocket14;
    private static final String LOG_FILE = "../log/client1.txt";
    private static Socket clientSocket, socket12, socket13, socket14;
    private static PrintWriter out;//, out12, out13, out14;
    private static BufferedReader in;//, in12, in13, in14;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static int cnt12, cnt13, cnt14;
    private static String[] message1, message2;

    public static void main(String[] args) throws IOException {
        socketConnection();
        
        while(round <= 100) {
            if(in.readLine().equals("[ALERT] ROUND START"))
                init();
            
            ClientThread client1 = new ClientThread(socket13, 1, socket14, 2);
            ClientThread client2 = new ClientThread(socket12, 1, socket14, 3);
            ClientThread client3 = new ClientThread(socket12, 1, socket13, 4);
            
            List<Thread> threads = new ArrayList<>();
            Thread thread1 = new Thread(client1);
            Thread thread2 = new Thread(client2);
            Thread thread3 = new Thread(client3);
            threads.add(thread1);
            threads.add(thread2);
            threads.add(thread3);

            thread1.start();
            thread2.start();
            thread3.start();

            // 수정 필요할 듯
            for(Thread thread : threads){
                try{
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        
            round++;
        }
        closeSocket();
    }

    private static void socketConnection() throws IOException {
        clientSocket = new Socket("127.0.0.1", PORT);
        serverSocket12 = new ServerSocket(PORT12);
        serverSocket13 = new ServerSocket(PORT13);
        serverSocket14 = new ServerSocket(PORT14);
        socket12 = serverSocket12.accept();
        socket13 = serverSocket13.accept();
        socket14 = serverSocket14.accept();

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        //out12 = new PrintWriter(socket12.getOutputStream(), true);
        //in12 = new BufferedReader(new InputStreamReader(socket12.getInputStream()));
        //out13 = new PrintWriter(socket13.getOutputStream(), true);
        //in13 = new BufferedReader(new InputStreamReader(socket13.getInputStream()));
        //out14 = new PrintWriter(socket14.getOutputStream(), true);
        //in14 = new BufferedReader(new InputStreamReader(socket14.getInputStream()));
    }

    private static void setMatrix() {
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++)
                matrix[i][j] = (int)(Math.random() * 100);
        }
    }
    
    private static void init(){
        setMatrix();
        cnt12 = cnt13 = cnt14 = 0;
    }

    private static String getClientMessage(int cnt, int mode) {
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
    }
}
