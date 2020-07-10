package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.windows.AddNewCardWnd;
import sample.windows.WindowManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;



public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        GridPane root = (GridPane)FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setTitle(AppProps.getInstance().GetProperty(AppProps.Property.APP_NAME));

        List<DatabaseController.Deck> decks = DatabaseController.getInstance().GetAllDecks();

        for (int i = 0; i < decks.size(); ++i) {
            Button deckBtn = new Button(decks.get(i).name);
            int finalI = i;
            deckBtn.setOnAction(actionEvent -> {
                OnClickDeck(decks.get(finalI).id);
            });

            root.add(deckBtn, 0, i + 1);
        }

        Scene scene = new Scene(root, 600, 600);

        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

        System.out.println("INIT");
        WindowManager.getInstance().ShowWindow(WindowManager.WindowType.ADD_NEW_CARD);
    }

    private void OnClickDeck(int deckId) {
        System.out.println("deck " + deckId);

        SwitchToStudyDeckWindow(deckId);
    }

    private void SwitchToStudyDeckWindow(int deckId) {
        List<DatabaseController.Card> cards = DatabaseController.getInstance().GetAllCardsForDeck(deckId);

        ScrollPane sp = new ScrollPane();
        GridPane gridPane = new GridPane();

        sp.setContent(gridPane);

        for (int i = 0; i < cards.size(); ++i) {
            gridPane.add(new Text(cards.get(i).question), 0, i + 1);
            for (int j = 0; j < cards.get(i).imagePathList.size(); ++j) {
                try {
                    Image img = new Image(new FileInputStream(cards.get(i).imagePathList.get(j)));
                    ImageView imageView = new ImageView(img);
                    gridPane.add(imageView, 0, 10 + j);
                } catch (FileNotFoundException ex) {
                    System.out.println("try to open file: " + cards.get(i).imagePathList.get(j));
                    ex.printStackTrace();
                }
            }
        }

        Stage stage = new Stage();
        stage.setTitle("Cards for this deck");
        stage.setScene(new Scene(sp, 450, 450));
        stage.show();
    } // todo (separate class)

    public static void main(String[] args) {
        DatabaseController.getInstance().Connect();
        launch(args);
        DatabaseController.getInstance().Disconnect();
    }
}
