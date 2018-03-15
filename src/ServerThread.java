import sun.plugin.javascript.navig4.Link;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.Vector;

public class ServerThread extends Thread {
        protected Socket socket = null;
        protected PrintWriter out = null;
        protected BufferedReader in = null;
        protected Vector messages = null;

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
                } else if (in.readLine().split(" ")[0].equalsIgnoreCase("DOWNLOAD")) {

                } else if (in.readLine().split(" ")[0].equalsIgnoreCase("UPLOAD")) {
                    int bytesRead;
                    int current = 0;
                    FileOutputStream fos;
                    BufferedOutputStream bos;

                    try {
                        String[] words = in.readLine().split(" ");
                        File currentFile = new File(words[1]);
                        int fileLength = Integer.parseInt(in.readLine());

                        InputStream is = socket.getInputStream();
                        byte[] mybytearray = new byte[fileLength];
                        fos = new FileOutputStream(currentFile);
                        bos = new BufferedOutputStream(fos);
                        bytesRead = is.read(mybytearray, 0, mybytearray.length);

                        current = bytesRead;

                        do {
                            bytesRead = is.read(mybytearray, current, (mybytearray.length-current));
                            if(bytesRead >= 0) current += bytesRead;
                        } while(bytesRead < -1);

                        bos.write(mybytearray, 0 , current);
                        bos.flush();
                        System.out.println("File " + currentFile  + " Uploaded (" + current + " bytes read)");
                        System.out.println("Done.");

                        is.close();
                        fos.close();
                        bos.close();
                        // updating folder which currently has all shared files
                        currentFolder.add(currentFile);

                        // sending folder names of files back to client for display
                        for (File f : currentFolder) {
                            out.println(f.getName());
                        }

                    } catch(IOException e) {
                        e.printStackTrace();
                        System.out.println("");
                    }
                }
            } catch(IOException e) {
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

    public LinkedList<File> getCurrentFolder() {
        return currentFolder;
    }

}
