import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Main extends Application {

    private BorderPane layout = new BorderPane();
    private TableView<Files> client = new TableView<>();
    private TableView<Files> server = new TableView<>();
    private Button downloadBtn = new Button("Download");
    private Button uploadBtn = new Button("Upload");
    private GridPane editArea = new GridPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("File Sharer v1.0 Bro");
        primaryStage.setScene(new Scene(layout, 500, 600));
        primaryStage.show();

        editArea.add(downloadBtn, 0, 0);
        editArea.add(uploadBtn, 1, 0);
        downloadBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){

            }
        });

        uploadBtn.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){

            }
        });

        layout.setTop(editArea);
        layout.setLeft(client);
        layout.setRight(server);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
