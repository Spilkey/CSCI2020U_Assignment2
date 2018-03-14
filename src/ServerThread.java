import sun.plugin.javascript.navig4.Link;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServerThread extends Thread {
        protected Socket socket       = null;
        protected PrintWriter out     = null;
        protected BufferedReader in   = null;
        protected Vector messages     = null;

        protected LinkedList<File> currentFolder= null;

        public ServerThread(Socket socket) {
            super();
            this.socket = socket;
            this.messages = messages;
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("IOEXception while opening a read/write connection");
            }
        }

        public void run() {
            // initialize interaction
            try {
                if (in.readLine().equalsIgnoreCase("DIR")) {
                    for (File f : currentFolder) {
                        out.println(f.getName());
                    }


                } else if (in.readLine().equalsIgnoreCase("DOWNLOAD")) {


                } else if (in.readLine().equalsIgnoreCase("UPLOAD")) {

                }
            }catch(IOException e){
                e.printStackTrace();
            }

            try {
                socket.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }



        public void setCurrentFolder(LinkedList<File> currentFolder) {
            this.currentFolder = currentFolder;
    }
}
