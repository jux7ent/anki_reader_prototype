package sample.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sample.misc.Vector2;
import sample.AppProps;
import sample.DatabaseController;
import sample.Screenshotter;



public class AddNewCardWnd extends AbstractGridPaneWindow implements DatabaseController.IDatabaseUpdate {

    private HashMap<String, DatabaseController.Deck> _deckNameToDeck;
    private HashMap<Integer, List<String>> _deckIdToCardsQuestion;

    private final String _resourcePath = "resources/add_new_card.fxml";

    private Button _addBtn;
    private ComboBox _decksDropDown;
    private ComboBox _cardsDropDown;

    @Override
    protected void OnInitWindow(Stage stage, GridPane root, Scene scene) {
        DatabaseController.getInstance().onDatabaseUpdate.add(this);

        System.out.println("OnInitWindow");
        _deckNameToDeck = new HashMap<>();
        _deckIdToCardsQuestion = new HashMap<>();

        stage.setTitle("Add question");
        stage.setAlwaysOnTop(true);

        SetUIElementsAndAddListeners(root);
    }

    @Override
    protected String GetResourcePath() {
        return _resourcePath;
    }

    protected void SetUIElementsAndAddListeners(GridPane root) {
        System.out.println("SetUIElementsAndAddListeners");
        SetUIElementsById(root);

        List<DatabaseController.Deck> decksList = DatabaseController.getInstance().GetAllDecks();
        for (DatabaseController.Deck deck : decksList) {
            _deckNameToDeck.put(deck.name, deck);
        }

        _addBtn.setOnAction(event -> {
            WindowManager.getInstance().ShowWindow(WindowManager.WindowType.MAKE_SCREENSHOT);
        });

        _decksDropDown.setOnAction(event -> {
            WindowManager.getInstance().choosedDeckID = _deckNameToDeck.get(_decksDropDown.getValue().toString()).id;
            UpdateCardsDropDownByDeck(WindowManager.getInstance().choosedDeckID);
        });

        UpdateDecksDropDown();
        UpdateCardsDropDownByDeck(_deckNameToDeck.get(_decksDropDown.getValue().toString()).id);
    }

    private void SetUIElementsById(GridPane root) {
        _addBtn = (Button) root.lookup("#addBtn");
        _decksDropDown = (ComboBox) root.lookup("#decksDropDown");
        _cardsDropDown = (ComboBox) root.lookup("#cardsDropDown");
    }

    private void UpdateDecksDropDown() {
        ObservableList<String> decksName = FXCollections.observableArrayList(_deckNameToDeck.keySet());
        _decksDropDown.setItems(decksName);
        _decksDropDown.setValue(decksName.get(0));
    }

    private void UpdateCardsDropDownByDeck(int deckId) {
        System.out.println("UpdateCardsDropDownByDeck");
        if (!_deckIdToCardsQuestion.containsKey(deckId)) {
            List<DatabaseController.Card> cardsForDeck =
                    DatabaseController.getInstance().GetAllCardsForDeck(deckId);

            List<String> cardsQuestionsForDeck = new ArrayList<>(cardsForDeck.size());
            for (DatabaseController.Card card : cardsForDeck) {
                cardsQuestionsForDeck.add(card.question);
            }

            _deckIdToCardsQuestion.put(deckId, cardsQuestionsForDeck);
        }

        ObservableList<String> cardsQuestions = FXCollections.observableArrayList(_deckIdToCardsQuestion.get(deckId));
        _cardsDropDown.setItems(cardsQuestions);
        _cardsDropDown.setValue(cardsQuestions.get(cardsQuestions.size() - 1));
    }

    @Override
    public void OnAddNewCard(String newCardName, int deckId) {
        if (_deckIdToCardsQuestion.containsKey(deckId)) {
            _deckIdToCardsQuestion.get(deckId).add(newCardName);
        }

        UpdateCardsDropDownByDeck(deckId);
    }

    @Override
    public void OnAddNewDeck(String newDeckName) {
        UpdateDecksDropDown();
    }
}
