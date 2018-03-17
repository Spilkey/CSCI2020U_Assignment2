import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;

public class Server {
    protected Socket clientSocket = null;
    protected ServerSocket serverSocket = null;
    protected ServerThread[] threads = null;

    protected String currentDir = System.getProperty("user.dir");
    private LinkedList<File> files = new LinkedList<>();

    protected int numClients = 0;

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
                threads[numClients] = new ServerThread(clientSocket);

                threads[numClients].setCurrentFolder(files);
                threads[numClients].start();
                this.setFiles(threads[numClients].getCurrentFolder());
                numClients++;
            }
        } catch (IOException e) {
            System.err.println("IOException while creating server connection");
        }

    }

    public static void main(String[] args) {
        Server app = new Server();
    }

    public void setFiles(LinkedList<File> files) {
        this.files = files;
    }
}
