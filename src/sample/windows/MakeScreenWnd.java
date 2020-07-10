package sample.windows;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.Screenshotter;
import sample.misc.Vector2;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MakeScreenWnd extends AbstractGridPaneWindow {
    private Vector2 _startPoint;
    private Rectangle _choosedRect;
    private Canvas _canvas;

    private final String _resourcePath = "resources/make_screen.fxml";

    @Override
    protected void OnInitWindow(Stage stage, GridPane root, Scene scene) {
        _startPoint = new Vector2();
        _choosedRect = new Rectangle();

        stage.initStyle(StageStyle.UNDECORATED);

        _canvas = InitCanvas();
        root.getChildren().add(_canvas);

        stage.setWidth(_canvas.getWidth());
        stage.setHeight(_canvas.getHeight());

        stage.setAlwaysOnTop(true);

        stage.setTitle(null);
        stage.setOpacity(0.5);
    }

    @Override
    protected String GetResourcePath() {
        return _resourcePath;
    }

    private Canvas InitCanvas() {
        // get screen size
        double screenWidth = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
        double screenHeight =  GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;

        _canvas = new Canvas(screenWidth, screenHeight);

        GraphicsContext graphicsContext = _canvas.getGraphicsContext2D();

        InitBrush(graphicsContext);

        _canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> { // save start point after click
            System.out.println("mouse pressed");
            _startPoint = new Vector2(event.getScreenX(), event.getScreenY());
        });

        _canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, // redraw rectangle while dragging mouse
                event -> {
                    graphicsContext.clearRect(0, 0, _canvas.getWidth(), _canvas.getHeight()); // clear prev drawn rect
                    int leftUpX = (int) Math.min(_startPoint.x, event.getScreenX());
                    int leftUpY = (int) Math.min(_startPoint.y, event.getScreenY());

                    _choosedRect.x = leftUpX;
                    _choosedRect.y = leftUpY;
                    _choosedRect.width = (int) Math.abs(_startPoint.x - event.getScreenX());
                    _choosedRect.height = (int) Math.abs(_startPoint.y - event.getScreenY());

                    graphicsContext.strokeRect(_choosedRect.x, _choosedRect.y, _choosedRect.width, _choosedRect.height);
                });

        _canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,  // take screenshot after mouse release
                event -> {
                    stage.setOpacity(0);

                    BufferedImage screenImage = Screenshotter.TakeScreenshot(_choosedRect);

                    if (screenImage != null) {
                        WindowCommunicator.getInstance().SetMessage(WindowCommunicator.MessageType.SAVE_CARD_IMG, screenImage);
                        WindowManager.getInstance().ShowWindow(WindowManager.WindowType.SAVE_CARD);

                        stage.setOpacity(0.5);
                        stage.close();
                    }
                });

        return _canvas;
    }

    private void InitBrush(GraphicsContext graphicsContext) {
        double canvasWidth = graphicsContext.getCanvas().getWidth();
        double canvasHeight = graphicsContext.getCanvas().getHeight();

        graphicsContext.setFill(javafx.scene.paint.Color.LIGHTGRAY);
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);

        graphicsContext.setFill(javafx.scene.paint.Color.RED);
        graphicsContext.setStroke(Color.BLUE);
        graphicsContext.setLineWidth(1);
    }
}
