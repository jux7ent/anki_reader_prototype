package sample;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Controller {
    @FXML
    public void PrintHello() {
        GridPane parent = new GridPane();
        Stage stage = new Stage();
        stage.setTitle("My New Stage Title");
        stage.setScene(new Scene(parent, 400, 400));
        stage.show();
    }
}
