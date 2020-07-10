package sample;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

public class DatabaseController {
    private interface ActionWithSqlStatement {
        void Invoke(Statement statement) throws SQLException;
    }

    private static DatabaseController _instance;
    private Connection _connection;

    private final String _getAllDecksQuery =
            "SELECT * FROM decks;";
    private final String _getAllCardByDeckIdQueryTemplate =
            "SELECT inner_table.cardId, question, study_stage, deckId, last_timestamp, GROUP_CONCAT(image_path SEPARATOR \"|\") as images\n" +
            "FROM (SELECT cards.cardId, GROUP_CONCAT(text SEPARATOR \"\\n\") as question, study_stage, deckId, last_timestamp\n" +
            "      FROM cards\n" +
            "               LEFT JOIN questions q on cards.cardId = q.cardId\n" +
            "      where cards.deckId={0}\n" +
            "      GROUP BY cards.cardId) as inner_table\n" +
            "         LEFT JOIN images ON images.cardId = inner_table.cardId\n" +
            "GROUP By images.cardId;";
    private final String _addNewCardTemplate =
            "INSERT INTO cards (study_stage, deckId, last_timestamp) VALUES ({0}, {1}, {2});\n" +
                    "INSERT INTO images (image_path, cardId) VALUES (\"{3}\", (SELECT MAX(cards.cardId) FROM cards));\n" +
                    "INSERT INTO questions (text, cardId) VALUES (\"{4}\", (SELECT cardId FROM images where imageId = (SELECT MAX(imageId) FROM images)));";
    private final String _addQuestionToCardTemplate =
            "INSERT INTO questions (text, cardId) VALUES ({0}, {1});";
    private final String _addImageToCardTemplate =
            "INSERT INTO images (image_path, cardId) VALUES ({0}, {1});";
    private final String _addNewDeckTemplate =
            "INSERT INTO decks (name) VALUES (\"{0}\");";

    public interface IDatabaseUpdate {
        void OnAddNewCard(String newCardName, int deckId);
        void OnAddNewDeck(String newDeckName);
    }

    public Set<IDatabaseUpdate> onDatabaseUpdate = new HashSet<>();

    public static class Deck {
        public int id;
        public String name;

        public Deck(int deckId, String deckName) {
            id = deckId;
            name = deckName;
        }
    }

    public static class Card {
        public int id;
        public String question;
        public List<String> imagePathList = new ArrayList<>();
        public int deckId;
        public int lastTimestamp;
        public int studyStage;

        public Card() {}

        public Card(int id, String question, List<String> imagePathList, int deckId, int lastTimestamp, int studyStage) {
            this.id = id;
            this.question = question;
            this.imagePathList = imagePathList;
            this.deckId = deckId;
            this.lastTimestamp = lastTimestamp;
            this.studyStage = studyStage;
        }
    }

    public static DatabaseController getInstance() {
        if (_instance == null) {
            _instance = new DatabaseController();
        }

        return _instance;
    }

    public void Connect() {
        AppProps props = AppProps.getInstance();

        try {
            Class.forName(props.GetProperty(AppProps.Property.DB_DRIVER_CLASS_NAME));
            _connection =
                    DriverManager.getConnection(
                            props.GetProperty(AppProps.Property.DB_URL_CONNECTION),
                            props.GetProperty(AppProps.Property.DB_USERNAME),
                            props.GetProperty(AppProps.Property.DB_PASSWORD)
                    );
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<Deck> GetAllDecks() {
        List<Deck> deckList = new ArrayList<>();

        SqlMakeQuery(statement -> {
            ResultSet queryResult = statement.executeQuery(_getAllDecksQuery);

            while (queryResult.next()) {
                deckList.add(new Deck(queryResult.getInt("deckId"), queryResult.getString("name")));
            }
        });

        return deckList;
    }

    public List<Card> GetAllCardsForDeck(int deckId) {
        List<Card> cardList = new ArrayList<>();

        SqlMakeQuery(statement -> {
            ResultSet cardsQueryResult =
                    statement.executeQuery(MessageFormat.format(_getAllCardByDeckIdQueryTemplate, deckId));

            while (cardsQueryResult.next()) {
                Card card = new Card();
                card.id = cardsQueryResult.getInt("cardId");
                card.studyStage = cardsQueryResult.getInt("study_stage");
                card.lastTimestamp = cardsQueryResult.getInt("last_timestamp");
                card.deckId = cardsQueryResult.getInt("deckId");
                card.question = cardsQueryResult.getString("question");
                // using the query we get paths separated by the symbol '|'
                card.imagePathList =
                        new ArrayList<String>(Arrays.asList(cardsQueryResult.getString("images").split("\\|")));

                cardList.add(card);
            }
        });

        return cardList;
    }

    public boolean AddNewDeck(String deckName) {
        return SqlMakeQuery(statement -> {
            statement.executeUpdate(MessageFormat.format(_addNewDeckTemplate, deckName));

            for (IDatabaseUpdate subscriber : onDatabaseUpdate) {
                subscriber.OnAddNewDeck(deckName);
            }
        });
    }

    public boolean AddNewCard(int deckId, String question, String imagePath) {
        return SqlMakeQuery(statement -> {
            String entireQuery = MessageFormat.format(_addNewCardTemplate, 0, deckId, 0, imagePath, question);
            String[] subQueries = entireQuery.split(";"); // split entire query on subqueries by ";"
            for (String subQuery : subQueries) {
                statement.execute(subQuery);
            }

            for (IDatabaseUpdate subscriber : onDatabaseUpdate) {
                subscriber.OnAddNewCard(question, deckId);
            }
        });
    }

    public boolean AddQuestionToCard(String question, int cardId) {
        return SqlMakeQuery(statement -> {
            statement.executeUpdate(
                    MessageFormat.format(_addQuestionToCardTemplate, question, cardId));
        });
    }

    public boolean AddImageToCard(String imagePath, int cardId) {
        return SqlMakeQuery(statement -> {
            statement.executeUpdate(
                    MessageFormat.format(_addImageToCardTemplate, imagePath, cardId));
        });
    }

    public boolean IsConnected() throws SQLException {
        return _connection != null && !_connection.isClosed();
    }

    public void Disconnect() {
        if (_connection != null) {
            try {
                _connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean SqlMakeQuery(ActionWithSqlStatement action) {
        try {
            if (!IsConnected()) {
                throw new SQLException("no connection");
            }

            Statement statement = _connection.createStatement();
            action.Invoke(statement);

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }
}
