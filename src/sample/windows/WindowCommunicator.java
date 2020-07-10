package sample.windows;

import java.util.HashMap;

public class WindowCommunicator {
    public enum MessageType {
        SAVE_CARD_IMG
    }

    private HashMap<String, Object> _messages = new HashMap<>();

    private static WindowCommunicator _instance;

    public static WindowCommunicator getInstance() {
        if (_instance == null) {
            _instance = new WindowCommunicator();
        }

        return _instance;
    }

    public Object GetMessage(MessageType messageType) {
        if (!_messages.containsKey(messageType.toString())) {
            return null;
        }

        return _messages.get(messageType.toString());
    }

    public void SetMessage(MessageType messageType, Object message) {
        _messages.put(messageType.toString(), message);
    }
}
