package sample.windows;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

abstract public class AbstractGridPaneWindow {
    protected Stage stage;

    protected AbstractGridPaneWindow() {
        InitStage();
    }

    public void Show() {
        stage.show();
    }

    public void Hide() {
        stage.hide();
    }

    abstract protected void OnInitWindow(Stage stage, GridPane root, Scene scene);
    abstract protected String GetResourcePath();

    private void InitStage() {
        try {
            stage = new Stage();
            GridPane root = (GridPane) FXMLLoader.load(getClass().getResource(GetResourcePath()));
            Scene scene = new Scene(root);
            stage.setScene(scene);

            OnInitWindow(stage, root, scene);
        } catch (IOException ex) {
            System.err.println("Error in init add new card window");
            ex.printStackTrace();
        }
    }
}
