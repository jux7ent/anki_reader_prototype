package sample.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sample.AppProps;
import sample.DatabaseController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Paths;
import java.util.List;

public class SaveCardWnd extends AbstractGridPaneWindow {
    private final String _resourcePath = "resources/save_card.fxml";

    private TextField _questionTextField;
    private ImageView _screenImageView;
    private Button _cancelBtn;
    private Button _saveBtn;

    private BufferedImage _bufferedImage;

    @Override
    protected void OnInitWindow(Stage stage, GridPane root, Scene scene) {
        SetUIElementsAndAddListeners(root);
    }

    protected void SetUIElementsAndAddListeners(GridPane root) {
        SetUIElementsById(root);

        _cancelBtn.setOnAction(event -> {
            stage.close();
        });

        _saveBtn.setOnAction(event -> {
            String imageDestPath =
                    Paths.get(
                            AppProps.getInstance().GetProperty(AppProps.Property.APP_IMAGES_PATH),
                            System.currentTimeMillis() + ".jpg"
                    ).toString();

            try {
                ImageIO.write(_bufferedImage, "jpg", new File(imageDestPath));
                DatabaseController.getInstance().AddNewCard(WindowManager.getInstance().choosedDeckID, _questionTextField.getText(), imageDestPath);
                stage.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void Show() {
        _bufferedImage =
                (BufferedImage) WindowCommunicator.getInstance().GetMessage(WindowCommunicator.MessageType.SAVE_CARD_IMG);

        Image convertedImage =
                SwingFXUtils.toFXImage(_bufferedImage, null); // convert from awt to javafx image

        _screenImageView.setImage(convertedImage);

        super.Show();
    }

    private void SetUIElementsById(GridPane root) {
        _questionTextField = (TextField) root.lookup("#questionTextField");
        _screenImageView = (ImageView) root.lookup("#screenImg");
        _cancelBtn = (Button) root.lookup("#cancelBtn");
        _saveBtn = (Button) root.lookup("#saveBtn");
    }

    @Override
    protected String GetResourcePath() {
        return _resourcePath;
    }
}
