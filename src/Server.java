//import java.io.*;
//import java.net.ServerSocket;
//import java.net.Socket;
//
//public class Server {
//
//    public static void main(String args[]){
//        try {
//            ServerSocket serverSocket = new ServerSocket(8080);
//            while (true) {
//                Socket socket = serverSocket.accept();
//                PrintWriter out = new PrintWriter(socket.getOutputStream());
//
//                InputStream inStream = socket.getInputStream();
//                InputStreamReader reader = new InputStreamReader(inStream);
//
//                BufferedReader in = new BufferedReader(reader);
//
//                String line = null;
//
//                while ((line = in.readLine()) != null) {
//
//                }
//
//
//                socket.close();
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//
//        }
//    }
//
//}

import javafx.stage.DirectoryChooser;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Server {
    protected Socket clientSocket           = null;
    protected ServerSocket serverSocket     = null;
    protected ServerThread[] threads        = null;


    protected String currentDir   = System.getProperty("user.dir");
    private LinkedList<File> files = new LinkedList<>();



    protected int numClients                = 0;
    protected Vector messages               = new Vector();

    public static int SERVER_PORT = 8091;
    public static int MAX_CLIENTS = 5;

    public Server() {
        try {

            File dir = new File(currentDir);
            files.addAll(Arrays.asList(dir.listFiles()));

            serverSocket = new ServerSocket(SERVER_PORT);
            threads = new ServerThread[MAX_CLIENTS];



            while(true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");
                threads[numClients] = new ServerThread(clientSocket, messages);

                threads[numClients].setCurrentFolder(files);
                threads[numClients].start();
                numClients++;

            }
        } catch (IOException e) {
            System.err.println("IOEXception while creating server connection");
        }

    }

    public static void main(String[] args) {
        Server app = new Server();
    }




}
