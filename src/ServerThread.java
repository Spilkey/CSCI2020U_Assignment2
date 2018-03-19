

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

import java.util.Vector;

public class ServerThread extends Thread {

        protected Socket socket = null;
        protected PrintWriter out = null;
        protected DataInputStream in = null;
        protected Vector messages = null;


        private File currentDir;
        protected LinkedList<File> currentFolder= null;

        public ServerThread(Socket socket) {
            super();
            this.socket = socket;

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("IOEXception while opening a read/write connection");
            }
        }

        public void run() {
            // initialize interaction

            System.out.println("hello");

            try {
                String line = in.readLine();
                String[] words = line.split(" ");
                System.out.println("lines is "+line);
                if (words[0].equalsIgnoreCase("DIR")) {

                    for (File f : currentFolder) {
                        out.println(f.getName());
                    }
                    out.close();
                    in.close();

                } else if (words[0].equalsIgnoreCase("DOWNLOAD")) {


                } else if (words[0].equalsIgnoreCase("UPLOAD")) {

                    int bytesRead;

                    FileOutputStream fos;
                    BufferedOutputStream bos;

                    try {

                        File currentFile = new File(currentDir.getPath(),words[1]);
                        int fileLength = Integer.parseInt(in.readLine());





                        fos = new FileOutputStream(currentFile);
                        bos = new BufferedOutputStream(fos);

                        //does not read 2nd file when uploading
                        byte[] bytes = new byte[fileLength];
                        int i = 0;

                        while(in.available() != 0 && i < fileLength){
                            bytes[i] = in.readByte();
                            i ++;
                        }

                        bos.write(bytes);

                        bos.flush();
                        fos.flush();

                       // System.out.println("File " + currentFile + " Uploaded (" + bytesRead + " bytes read)");
                        System.out.println("Done.");

                        in.close();
                        fos.close();
                        bos.close();

                        out.close();

                        // updating folder which currently has all shared files
                        currentFolder.add(currentFile);

                        // sending folder names of files back to client for display


                        System.out.println("current file is"+currentFile);
                        System.out.println(" ");




                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("");
                    }
                }


            } catch (IOException e) {

                e.printStackTrace();
            }

            //*********************************************************
            //TODO:: Open and close sockets per upload/download request
            //*********************************************************
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void setCurrentFolder(LinkedList<File> currentFolder) {

        this.currentFolder = currentFolder;

    }

    public LinkedList<File> getCurrentFolder() {
        return currentFolder;
    }

    public void setCurrentDir(File currentDir) {
        this.currentDir = currentDir;
    }
}
