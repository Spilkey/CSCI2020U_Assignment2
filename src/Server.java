import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String args[]){
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            while (true) {
                Socket socket = serverSocket.accept();
                PrintWriter out = new PrintWriter(socket.getOutputStream());

                InputStream inStream = socket.getInputStream();
                InputStreamReader reader = new InputStreamReader(inStream);

                BufferedReader in = new BufferedReader(reader);

                String line = null;

                while ((line = in.readLine()) != null) {

                }


                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();

        }
    }

}
