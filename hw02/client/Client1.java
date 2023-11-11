import java.net.*;
import java.io.*;

public class Client1 {
    private static final int PORT = 8000, PORT12 = 8012, PORT13 = 8013, PORT14 = 8014;
    private static ServerSocket serverSocket12, serverSocket13, serverSocket14;
    //private static final String LOG_FILE = "../log/client1.txt";
    private static Socket clientSocket, socket12, socket13, socket14;
    private static PrintWriter out, out12, out13, out14;
    private static BufferedReader in, in12, in13, in14;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static int cnt12, cnt13, cnt14;
    private static boolean[] visit12 = new boolean[10], visit13 = new boolean[10], visit14 = new boolean[10];

    public static void main(String[] args) throws IOException {
        socketConnection();
        
        init();
        String message12 = in12.readLine();
        String message13 = in13.readLine();
        System.out.println(calculate(message12, message13));
        
        closeSocket();
    }

    private static void socketConnection() throws IOException {
        //clientSocket = new Socket("127.0.0.1", PORT);
        serverSocket12 = new ServerSocket(PORT12);
        serverSocket13 = new ServerSocket(PORT13);
        serverSocket14 = new ServerSocket(PORT14);
        socket12 = serverSocket12.accept();
        socket13 = serverSocket13.accept();
        socket14 = serverSocket14.accept();

        //out = new PrintWriter(clientSocket.getOutputStream(), true);
        //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out12 = new PrintWriter(socket12.getOutputStream(), true);
        in12 = new BufferedReader(new InputStreamReader(socket12.getInputStream()));
        out13 = new PrintWriter(socket13.getOutputStream(), true);
        in13 = new BufferedReader(new InputStreamReader(socket13.getInputStream()));
        out14 = new PrintWriter(socket14.getOutputStream(), true);
        in14 = new BufferedReader(new InputStreamReader(socket14.getInputStream()));
    }

    private static void setMatrix() {
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++)
                matrix[i][j] = (int)(Math.random() * 100);
        }
    }
    
    private static void init(){
        setMatrix();
        for(int i = 0; i < 10; i++)
            visit12[i] = visit13[i] = visit14[i] = false;
        cnt12 = cnt13 = cnt14 = 0;
    }

    private static String getMessage(int cnt, int mode){
        String message = "";
        if(mode == 1){
            for(int i = 0; i < 10; i++){
                message += matrix[cnt][i] + " ";
            }
        }
        return message;
    }

    private static int calculate(String msg1, String msg2) {
        String[] mat1 = msg1.split(" "), mat2 = msg2.split(" ");
        int result = 0;
        for(int i = 0; i < 10; i++){
            result += Integer.parseInt(mat1[i]) * Integer.parseInt(mat2[i]);
        }
        return result;
    }

    /*
    private static void log(String message) {
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
			out.println(message);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
    */

    private static void closeSocket() {
        try {
            serverSocket12.close();
            serverSocket13.close();
            serverSocket14.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
