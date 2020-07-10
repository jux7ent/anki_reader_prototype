package sample.windows;

import com.sun.corba.se.spi.activation._InitialNameServiceImplBase;

import java.util.HashMap;

public class WindowManager {
    public enum WindowType {
        START,
        ADD_NEW_CARD,
        MAKE_SCREENSHOT,
        SAVE_CARD
    }

    public int choosedDeckID = -1; // =(
    public int choosedScreenID = -1;

    private AbstractGridPaneWindow _startWnd;
    private AbstractGridPaneWindow _addNewCardWnd;
    private AbstractGridPaneWindow _makeScreenshotWnd;
    private AbstractGridPaneWindow _saveCardWnd;

    private static WindowManager _instance;

    public static WindowManager getInstance() {
        if (_instance == null) {
            _instance = new WindowManager();
        }

        return _instance;
    }

    public void ShowWindow(WindowType windowType) {
        GetWindowByType(windowType).Show();
    }

    private WindowManager() {
        _addNewCardWnd = new AddNewCardWnd();
        _makeScreenshotWnd = new MakeScreenWnd();
        _saveCardWnd = new SaveCardWnd();

        _addNewCardWnd.Hide();
        _makeScreenshotWnd.Hide();
        _saveCardWnd.Hide();
    }

    private AbstractGridPaneWindow GetWindowByType(WindowType windowType) {
        System.out.println("GetWindowByType: " + windowType.toString());
        switch (windowType) {
            case START: {
                return null;
            }
            case ADD_NEW_CARD: {
                return _addNewCardWnd;
            }
            case MAKE_SCREENSHOT: {
                return _makeScreenshotWnd;
            }
            case SAVE_CARD: {
                return _saveCardWnd;
            }
        }

        return null;
    }
}
