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
import static javax.swing.text.html.HTML.Tag.HEAD;

public class Client extends Application {
    // Socket and reader/writer
    private Socket socket = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;

    private File clientDir;



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

            }


    public void start(Stage primaryStage) throws Exception {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));

        directoryChooser.setTitle("Open the directory for the client");
        clientDir = directoryChooser.showDialog(primaryStage);

        addClientFiles();


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




        primaryStage.setTitle("File Sharer v1.0 Bro");
        primaryStage.setScene(new Scene(layout, 500, 600));
        primaryStage.show();


        editArea.add(downloadBtn, 0, 0);
        editArea.add(uploadBtn, 1, 0);
        //TODO COMPLETE DOWNLOAD BUTTON
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
                /*
                Checks for a user to have a File selected from
                the view table upon clicking the download button

                if  not tells them to select from the table
                */
                try {
                    upFile = new File(client.getSelectionModel().getSelectedItem());
                }catch(NullPointerException e){
                    System.out.println("You need to select a item for the table");
                    e.printStackTrace();
                    return;
                }

                //Socket, I/O Setup
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


                // Sending UPLOAD filename command to Server
                networkOut.println("UPLOAD "+ upFile.getName());

                // Initializing Separate Streams for file reading/writing
                DataOutputStream dos;
                FileInputStream fis;
                BufferedInputStream bis;

                // Sending the File length to the server
                networkOut.println(upFile.length());

                try {
                    // Reading in the file and writing the file
                    byte [] mybytearray  = new byte [(int)upFile.length()];
                    fis = new FileInputStream(upFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(mybytearray,0,mybytearray.length);

                    dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Sending " + upFile + "(" + mybytearray.length + " bytes)");


                    DataInputStream in = new DataInputStream(new FileInputStream(upFile));
                    int count;
                    while ((count = in.read(mybytearray)) > 0) {
                        dos.write(mybytearray , 0, count);
                    }

                    //TODO Problem here
                    dos.write(mybytearray,0,mybytearray.length);
                    //Problem here


                    dos.flush();
                    System.out.println("Done.");

                    //Reading output from the server to look add to the listveiw
                    while(networkIn.readLine() != null) {
                        String s = networkIn.readLine();
                        server.getItems().add(s);
                    }

                } catch(IOException e) {
                    e.printStackTrace();
                    System.out.println("1 or more streams failed");
                }

                //closing the streams
                try{
                    socket.close();
                    networkOut.close();
                    networkIn.close();
                }catch(IOException e){
                    e.printStackTrace();
                }


            }
        });

        layout.setTop(editArea);
        layout.setLeft(client);
        layout.setRight(server);

        /*
        Below code calls the DIR Function which will grab the files from the server for display
         */

        //DIR
        //*****************************


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
        networkOut.println("DIR");
        while(networkIn.readLine() != null) {
            String s = networkIn.readLine();
            server.getItems().add(s);
        }

        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        //***************************





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

    public void addClientFiles(){

        if(!clientDir.isDirectory()){
            System.out.println(clientDir+"is not a dirctory");
        }else{
            for (String x: clientDir.list()){
                client.getItems().add(x);
            }
        }

    }
}

