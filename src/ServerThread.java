import java.io.*;
import java.net.Socket;
import java.util.LinkedList;

public class ServerThread extends Thread {
        protected Socket socket = null;
        protected PrintWriter out = null;
        protected DataInputStream in = null;

        private File currentDir;
        protected LinkedList<File> currentFolder = null;

        public ServerThread(Socket socket) {
            super();
            this.socket = socket;

            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("IOException while opening a read/write connection");
            }
        }

        public void run() {
            // initialize interaction
            try {
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                String line = in.readLine();
                String[] words = line.split(",");
                if (words[0].equalsIgnoreCase("DIR")) {
                    try {
                        out = new PrintWriter(socket.getOutputStream(), true);

                    } catch (IOException e) {
                        System.err.println("IOException while opening a read/write connection");
                    }

                    for (File f : currentFolder) {
                        out.println(f.getName());
                    }
                    out.close();
                    in.close();
                }else if( words[0].equalsIgnoreCase("DOWNLOAD")){
                    // Initialize streams
                    DataOutputStream dos;
                    FileInputStream fis;
                    BufferedInputStream bis;

                    // Sending the File length to the server
                    try {
                        dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                        File reqFile = new File(currentDir+"\\"+words[1]);
                        dos.writeBytes(String.valueOf(reqFile.length())+'\n');

                        // Reading in the file and writing the file
                        byte[] byteArr  = new byte [(int)reqFile.length()];
                        fis = new FileInputStream(reqFile);
                        bis = new BufferedInputStream(fis);
                        int bytesread = bis.read(byteArr,0,byteArr.length);

                        System.out.println("Reading " + reqFile + "(" + byteArr.length + " bytes)");
                        System.out.println("Read "+ bytesread);
                        DataInputStream ins = new DataInputStream(new FileInputStream(reqFile));
                        int count;
                        while ((count = ins.read(byteArr)) > 0) {
                            System.out.println(count);
                            dos.write(byteArr , 0, count);
                            System.out.println("writing");
                        }

                        dos.flush();
                        dos.close();
                        bis.close();
                        fis.close();
                        ins.close();
                        in.close();
                        socket.close();

                    } catch(IOException e) {
                        e.printStackTrace();
                        System.out.println("1 or more streams failed");
                    }
                }else if (words[0].equalsIgnoreCase("UPLOAD")){

                    BufferedOutputStream bos;

                    try {
                        out = new PrintWriter(socket.getOutputStream(), true);

                        File currentFile = new File(currentDir.getPath(), words[1]);
                        int fileLength = Integer.parseInt(in.readLine());

                        bos = new BufferedOutputStream(new FileOutputStream(currentFile));

                        byte[] byteArr = new byte[fileLength];
                        int i = 0;

                        while(in.available() != 0 && i < fileLength) {
                            byteArr[i] = in.readByte();
                            i++;
                        }

                        bos.write(byteArr);
                        bos.flush();

                        in.close();
                        out.close();
                        bos.close();

                        // updating folder which currently has all shared files
                        currentFolder.add(currentFile);

                        // sending folder names of files back to client for display
                        System.out.println("current file is " + currentFile);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public LinkedList<File> getCurrentFolder() { return currentFolder; }

    public void setCurrentFolder(LinkedList<File> currentFolder) { this.currentFolder = currentFolder; }

    public void setCurrentDir(File currentDir) { this.currentDir = currentDir; }
}
