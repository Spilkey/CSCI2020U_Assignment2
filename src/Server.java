
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Vector;

public class Server {
    protected Socket clientSocket = null;
    protected ServerSocket serverSocket = null;
    protected ServerThread[] threads = null;

    protected File currentDir = new File("ServerFiles");
    private LinkedList<File> files = new LinkedList<>();

    protected int numClients = 0;

    public static int SERVER_PORT = 8091;
    public static int MAX_CLIENTS = 100;

    public Server() {
        try {

            files.addAll(Arrays.asList(currentDir.listFiles()));

            serverSocket = new ServerSocket(SERVER_PORT);
            LinkedList<ServerThread> threads = new LinkedList<>();

            while(true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");

                ServerThread currentThread = new ServerThread(clientSocket);
                threads.add(currentThread);
                int index = threads.indexOf(currentThread);

                threads.get(index).setCurrentFolder(files);
                threads.get(index).setCurrentDir(currentDir);
                threads.get(index).start();
                this.setFiles(threads.get(index).getCurrentFolder());
                threads.remove(threads.get(index));



                numClients++;
            }
        } catch (IOException e) {
            System.err.println("IOEXception while creating server connection");
        }

    }

    public static void main(String[] args) {
        Server app = new Server();
    }

    public void setFiles(LinkedList<File> files) {
        this.files = files;
    }
}
