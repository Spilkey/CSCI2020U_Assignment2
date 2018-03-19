//TODO: User directory is supposed be a command line argument

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Client extends Application {
    // Socket and reader/writer
    private Socket socket = null;
    private PrintWriter networkOut = null;
    private BufferedReader networkIn = null;

    private File clientDir;
    private File serverDir;

    // holds the files
    private BorderPane layout = new BorderPane();
    // hold the file names for display
    private ListView<String> client = new ListView<>();
    private ListView<String> server = new ListView<>();

    private javafx.scene.control.Button downloadBtn = new Button("Download");
    private javafx.scene.control.Button uploadBtn = new Button("Upload");
    private GridPane editArea = new GridPane();

    public static String SERVER_ADDRESS = "localhost";
    public static int    SERVER_PORT = 8091;

    public void start(Stage primaryStage) throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("."));

        directoryChooser.setTitle("Open the directory for the client");
        clientDir = directoryChooser.showDialog(primaryStage);
        serverDir = new File("ServerFiles");

        addClientFiles();

        primaryStage.setTitle("File Sharer v1.0!");
        primaryStage.setScene(new Scene(layout, 500, 600));
        primaryStage.show();

        editArea.add(downloadBtn, 0, 0);
        editArea.add(uploadBtn, 1, 0);
        // TODO COMPLETE DOWNLOAD BUTTON
        downloadBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>(){
            @Override
            public void handle(javafx.event.ActionEvent event){
                File downFile;
                try {
                    downFile = new File(System.getProperty("user.dir")
                            +"\\"+serverDir.getName()+"\\"+server.getSelectionModel().getSelectedItem());
                } catch(NullPointerException e) {
                    System.out.println("Please select an item for the table");
                    e.printStackTrace();
                    return;
                }






            }
        });
        uploadBtn.setOnAction(new EventHandler<javafx.event.ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                File upFile;
                // Checks for a user to have a File selected from the ListView upon clicking the upload button
                try {
                    upFile = new File(System.getProperty("user.dir")
                            +"\\"+clientDir.getName()+"\\"+client.getSelectionModel().getSelectedItem());
                } catch(NullPointerException e) {
                    System.out.println("Please select an item for the table.");
                    e.printStackTrace();
                    return;
                }


                //Socket, I/O Setup
                try {
                    socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                } catch (UnknownHostException e) {
                    System.err.println("Unknown host: "+SERVER_ADDRESS);
                } catch (IOException e) {
                    System.err.println("IOException while connecting to server: "+SERVER_ADDRESS);
                }
                if (socket == null) {
                    System.err.println("socket is null");
                }
                try {
                    networkOut = new PrintWriter(socket.getOutputStream(), true);
                    networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                } catch (IOException e) {
                    System.err.println("IOException while opening a read/write connection");
                }

                // Sending UPLOAD filename command to Server
                networkOut.println("UPLOAD,"+ upFile.getName());

                // Initializing separate streams for file reading/writing
                DataOutputStream dos;
                FileInputStream fis;
                BufferedInputStream bis;

                // Sending the File length to the server
                networkOut.println(upFile.length());
                try {
                    // Reading in the file and writing the file
                    byte[] byteArr  = new byte [(int)upFile.length()];
                    fis = new FileInputStream(upFile);
                    bis = new BufferedInputStream(fis);
                    bis.read(byteArr,0,byteArr.length);

                    dos = new DataOutputStream(socket.getOutputStream());
                    System.out.println("Sending " + upFile + "(" + byteArr.length + " bytes)");

                    DataInputStream in = new DataInputStream(new FileInputStream(upFile));
                    int count;
                    while ((count = in.read(byteArr)) > 0) {
                        dos.write(byteArr , 0, count);
                    }

                    dos.flush();

                    //Reading output from the server to add to the ListView
                    server.getItems().add(upFile.getName());

                    networkOut.close();
                    networkIn.close();

                    dos.close();
                    bis.close();
                    fis.close();
                    in.close();
                    socket.close();

                } catch(IOException e) {
                    e.printStackTrace();
                    System.out.println("1 or more streams failed");
                }


            }
        });

        layout.setTop(editArea);
        layout.setLeft(client);
        layout.setRight(server);

        // Below code calls the DIR Function which will grab the files from the server for display
        // *****************************
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: "+SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("IOException while connecting to server: "+SERVER_ADDRESS);
        }
        if (socket == null) {
            System.err.println("socket is null");
        }
        try {
            networkOut = new PrintWriter(socket.getOutputStream(), true);
            networkIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("IOException while opening a read/write connection");
        }
        networkOut.println("DIR");
        String s = networkIn.readLine();
        while(s != null) {

            System.out.println(s);
            server.getItems().add(s);
            s = networkIn.readLine();
        }

        try{
            socket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        // *****************************
    }

    public static void main(String[] args) { launch(args); }

    public void addClientFiles(){
        if(!clientDir.isDirectory()){
            System.out.println(clientDir+"is not a directory");
        }else{
            for (String x: clientDir.list()){
                client.getItems().add(x);
            }
        }
    }
}
