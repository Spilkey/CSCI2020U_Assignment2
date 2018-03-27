import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class Server {
    protected Socket clientSocket = null;
    protected ServerSocket serverSocket = null;
    protected int numClients = 0;
    public static int SERVER_PORT = 8091;

    public Server() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            LinkedList<ServerThread> threads = new LinkedList<>();

            while(true) {
                clientSocket = serverSocket.accept();
                System.out.println("Client #"+(numClients+1)+" connected.");

                ServerThread currentThread = new ServerThread(clientSocket);
                threads.add(currentThread);
                int index = threads.indexOf(currentThread);

                threads.get(index).start();
                threads.remove(threads.get(index));

                numClients++;
            }
        } catch (IOException e) {
            System.err.println("IOException while creating server connection");
        }
    }
    public static void main(String[] args) {
        Server app = new Server();
    }
}
