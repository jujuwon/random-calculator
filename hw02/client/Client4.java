import java.net.*;
import java.io.*;

public class Client4 {
    private static final int PORT = 8000, PORT14 = 8014, PORT24 = 8024, PORT34 = 8034;
    //private static final String LOG_FILE = "../log/client4.txt";
    private static Socket clientSocket, socket14, socket24, socket34;
    private static PrintWriter out, out14, out24, out34;
    private static BufferedReader in, in14, in24, in34;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static int cnt14, cnt24, cnt34;
    private static boolean[] visit14 = new boolean[10], visit24 = new boolean[10], visit34 = new boolean[10];

    public static void main(String[] args) throws IOException {
        socketConnection();
        
        out14.println("Client4->1");
        System.out.println(in14.readLine());
        setMatrix();
    }

    private static void socketConnection() throws IOException {
        //clientSocket = new Socket("127.0.0.1", PORT);
        socket14 = new Socket("127.0.0.1", PORT14);
        socket24 = new Socket("127.0.0.1", PORT24);
        socket34 = new Socket("127.0.0.1", PORT34);

        //out = new PrintWriter(clientSocket.getOutputStream(), true);
        //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out14 = new PrintWriter(socket14.getOutputStream(), true);
        in14 = new BufferedReader(new InputStreamReader(socket14.getInputStream()));
        out24 = new PrintWriter(socket24.getOutputStream(), true);
        in24 = new BufferedReader(new InputStreamReader(socket24.getInputStream()));
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
            visit14[i] = visit24[i] = visit34[i] = false;
        cnt14 = cnt24 = cnt34 = 0;
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
}
