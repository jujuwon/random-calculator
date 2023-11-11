import java.net.*;
import java.io.*;

public class Client2 {
    private static final int PORT = 8000, PORT12 = 8012, PORT23 = 8023, PORT24 = 8024;
    private static ServerSocket serverSocket23, serverSocket24;
    //private static final String LOG_FILE = "../log/client2.txt";
    private static Socket clientSocket, socket12, socket23, socket24;
    private static PrintWriter out, out12, out23, out24;
    private static BufferedReader in, in12, in23, in24;
    private static int[][] matrix = new int[10][10];
    private static int round = 1;
    private static int cnt12, cnt23, cnt24;
    private static boolean[] visit12 = new boolean[10], visit23 = new boolean[10], visit24 = new boolean[10];

    public static void main(String[] args) throws IOException {
        socketConnection();
        
        init();
        out12.println(getMessage(0, 1));
        System.out.println(getMessage(0, 1));

        closeSocket();
    }

    private static void socketConnection() throws IOException {
        //clientSocket = new Socket("127.0.0.1", PORT);
        serverSocket23 = new ServerSocket(PORT23);
        serverSocket24 = new ServerSocket(PORT24);
        socket23 = serverSocket23.accept();
        socket24 = serverSocket24.accept();
        socket12 = new Socket("127.0.0.1", PORT12);

        //out = new PrintWriter(clientSocket.getOutputStream(), true);
        //in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out12 = new PrintWriter(socket12.getOutputStream(), true);
        in12 = new BufferedReader(new InputStreamReader(socket12.getInputStream()));
        out23 = new PrintWriter(socket23.getOutputStream(), true);
        in23 = new BufferedReader(new InputStreamReader(socket23.getInputStream()));
        out24 = new PrintWriter(socket24.getOutputStream(), true);
        in24 = new BufferedReader(new InputStreamReader(socket24.getInputStream()));
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
            visit12[i] = visit23[i] = visit24[i] = false;
        cnt12 = cnt23 = cnt24 = 0;
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
            serverSocket23.close();
            serverSocket24.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
