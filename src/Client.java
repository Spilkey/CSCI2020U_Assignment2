import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.util.List;

import static javafx.application.Application.launch;

public class Client extends Application {
    // Socket and reader/writer
    private Socket socket = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;

    // holds the files
    private LinkedList<File> localFiles = null;
    private BorderPane layout = new BorderPane();
    // hold the file names for display
    private ListView<String> client = new ListView<>();
    private ListView<String> server = new ListView<>();

    private javafx.scene.control.Button downloadBtn = new javafx.scene.control.Button("Download");
    private javafx.scene.control.Button uploadBtn = new Button("Upload");
    private GridPane editArea = new GridPane();

    public  static String SERVER_ADDRESS = "localhost";
    public  static int    SERVER_PORT = 8091;

    public Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("IOEXception while connecting to server: "+SERVER_ADDRESS);
        }
        if (socket == null) {
            System.err.println("socket is null");
        }
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOEXception while opening a read/write connection");
        }
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("File Sharer v1.0 Bro");
        primaryStage.setScene(new Scene(layout, 500, 600));
        primaryStage.show();

        editArea.add(downloadBtn, 0, 0);
        editArea.add(uploadBtn, 1, 0);
        downloadBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>(){
            @Override
            public void handle(javafx.event.ActionEvent event){
                String downFile;
                try {
                    while(networkIn.readLine() != null) {
                        downFile = networkIn.readLine();
                        client.getItems().add(downFile);
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

        uploadBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>(){
            @Override
            public void handle(ActionEvent event){

                File upFile;
                List<File> filesToAdd = new LinkedList<>();

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Please select file(s) to upload, type ");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
                upFile = fileChooser.showOpenDialog(primaryStage);

                networkOut.println("UPLOAD "+ upFile.getName());
                DataOutputStream dos;

                FileInputStream fis;
                BufferedInputStream bis;
                networkOut.println(upFile.length());

                try {
                    byte [] mybytearray  = new byte [(int)upFile.length()];
                    fis = new FileInputStream(upFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);

                    dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Sending " + upFile + "(" + mybytearray.length + " bytes)");

                    //TODO Problem here
                    dos.write(mybytearray,0,mybytearray.length);
                    //Problem here

                    dos.flush();
                    System.out.println("Done.");

                    while(networkIn.readLine() != null) {
                        String s = networkIn.readLine();
                        server.getItems().add(s);
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                    System.out.println("1 or more streams failed");
                }
            }
        });
       primaryStage.setOnCloseRequest(event ->  {
           try {
               socket.close();
               System.out.println("Socket Closed");
           }catch(IOException e){
               e.printStackTrace();
               System.out.println("Socket failed to close");
           }
        });
        layout.setTop(editArea);
        layout.setLeft(client);
        layout.setRight(server);

        networkOut.println("DIR");
        while(networkIn.readLine() != null) {
            String s = networkIn.readLine();
            server.getItems().add(s);
        }
    }

    public static void main(String[] args) {
        launch(args);

    }

    public void setLocalFiles(LinkedList<File> filesNames) {
        this.localFiles = filesNames;
    }
    protected int getErrorCode(String message) {
        StringTokenizer st = new StringTokenizer(message);
        String code = st.nextToken();
        return (new Integer(code)).intValue();
    }

    protected String getErrorMessage(String message) {
        StringTokenizer st = new StringTokenizer(message);
        String code = st.nextToken();
        String errorMessage = null;
        if (st.hasMoreTokens()) {
            errorMessage = message.substring(code.length()+1, message.length());
        }
        return errorMessage;
    }

    public void listAllMessages() {
        String message = null;

        networkOut.println("LASTMSG");
        // read response, store id
        int id = -1;
        try {
            message = networkIn.readLine();
        } catch (IOException e) {
            System.err.println("Error reading from socket.");
        }
        String strID = message.substring(message.indexOf(':')+1);
        id = (new Integer(strID.trim())).intValue();
        for (int i = 0; i <= id; i++) {
            networkOut.println("GETMSG "+i);
            try {
                message = networkIn.readLine();
            } catch (IOException e) {
                System.err.println("Error reading from socket.");
            }
            int index = message.indexOf(':')+1;
            String msg = message.substring(index);
            System.out.println(msg);
        }
        return errorMessage;
    }



    public void listAllMessages() {
        String message = null;

        networkOut.println("LASTMSG");

        // read response, store id
        int id = -1;
        try {
            message = networkIn.readLine();
        } catch (IOException e) {
            System.err.println("Error reading from socket.");
        }
        String strID = message.substring(message.indexOf(':')+1);
        id = (new Integer(strID.trim())).intValue();
        for (int i = 0; i <= id; i++) {
            networkOut.println("GETMSG "+i);
            try {
                message = networkIn.readLine();
            } catch (IOException e) {
                System.err.println("Error reading from socket.");
            }
            int index = message.indexOf(':')+1;
            String msg = message.substring(index);
            System.out.println(msg);
        }
    }


}
