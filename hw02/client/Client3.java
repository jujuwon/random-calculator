import java.net.*;
import java.io.*;

public class Client3 {
    private static final int PORT = 8000, PORT13 = 8013, PORT23 = 8023, PORT34 = 8034;
    private static ServerSocket serverSocket34;
    //private static final String LOG_FILE = "../log/client3.txt";
    private static Socket clientSocket, socket13, socket23, socket34;
    private static PrintWriter out, out13, out23, out34;
    private static BufferedReader in, in13, in23, in34;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static int cnt13, cnt23, cnt34;
    private static boolean[] visit13 = new boolean[10], visit23 = new boolean[10], visit34 = new boolean[10];

    public static void main(String[] args) throws IOException {
        socketConnection();

        init();
        out13.println(getMessage(0, 1));
        System.out.println(getMessage(0, 1));

        closeSocket();
    }

    private static void socketConnection() throws IOException {
        //clientSocket = new Socket("127.0.0.1", PORT);
        serverSocket34 = new ServerSocket(PORT34);
        socket34 = serverSocket34.accept();
        socket13 = new Socket("127.0.0.1", PORT13);
        socket23 = new Socket("127.0.0.1", PORT23);

        //out = new PrintWriter(clientSocket.getOutputStream(), true);
        //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out13 = new PrintWriter(socket13.getOutputStream(), true);
        in13 = new BufferedReader(new InputStreamReader(socket13.getInputStream()));
        out23 = new PrintWriter(socket23.getOutputStream(), true);
        in23 = new BufferedReader(new InputStreamReader(socket23.getInputStream()));
        out34 = new PrintWriter(socket34.getOutputStream(), true);
        in34 = new BufferedReader(new InputStreamReader(socket34.getInputStream()));
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
            visit13[i] = visit23[i] = visit34[i] = false;
        cnt13 = cnt23 = cnt34 = 0;
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
            serverSocket34.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
