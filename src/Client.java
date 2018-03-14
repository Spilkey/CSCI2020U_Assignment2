import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {
    public static void main(String args[]){
        try {
            Socket clientSocket = new Socket ("localhost", 8080);
            while (true) {

                PrintWriter out = new PrintWriter(clientSocket.getOutputStream());

                InputStream inStream = clientSocket.getInputStream();
                InputStreamReader reader = new InputStreamReader(inStream);

                BufferedReader in = new BufferedReader(reader);
                String line = null;

                while ((line = in.readLine()) != null) {
                    out.println();
                }


                clientSocket.close();
            }
        }catch(IOException e){
            e.printStackTrace();

        }
    }

}
