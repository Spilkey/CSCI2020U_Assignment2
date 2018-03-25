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
                String line = in.readLine();
                String[] words = line.split(",");
                if (words[0].equalsIgnoreCase("DIR")) {

                    for (File f : currentFolder) {
                        out.println(f.getName());
                    }
                    out.close();
                    in.close();
                }else if( words[0].equalsIgnoreCase("DOWNLOAD")){

                }else if (words[0].equalsIgnoreCase("UPLOAD")){
                    FileOutputStream fos;
                    BufferedOutputStream bos;

                    try {
                        File currentFile = new File(currentDir.getPath(), words[1]);
                        int fileLength = Integer.parseInt(in.readLine());

                        fos = new FileOutputStream(currentFile);
                        bos = new BufferedOutputStream(fos);

                        byte[] byteArr = new byte[fileLength];
                        int i = 0;

                        while(in.available() != 0 && i < fileLength) {
                            byteArr[i] = in.readByte();
                            i++;
                        }

                        bos.write(byteArr);

                        bos.flush();
                        fos.flush();

                        in.close();
                        out.close();
                        fos.close();
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
