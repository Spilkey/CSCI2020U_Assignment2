import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataSource {
    public static ObservableList<Files> getAllFiles() {
        ObservableList<Files> fileNames = FXCollections.observableArrayList();
        return fileNames;
    }
}
