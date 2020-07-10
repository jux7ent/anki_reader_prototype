package sample;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProps {
    public enum Property {
        DB_DRIVER_CLASS_NAME,
        DB_URL_CONNECTION,
        DB_USERNAME,
        DB_PASSWORD,
        APP_NAME,
        APP_IMAGES_PATH
    }

    private final String _propFilePath = "resources/config.properties";

    private static AppProps _instance;
    private Properties _properties = new Properties();

    private AppProps() { LoadData(); }

    public static AppProps getInstance() {
        if (_instance == null) {
            _instance = new AppProps();
        }

        return _instance;
    }

    public String GetProperty(Property property) {
        switch (property) {
            case DB_DRIVER_CLASS_NAME: {
                return _properties.getProperty("db.driver_class_name");
            }
            case DB_URL_CONNECTION: {
                return _properties.getProperty("db.url_connection");
            }
            case DB_USERNAME: {
                return _properties.getProperty("db.username");
            }
            case DB_PASSWORD: {
                return _properties.getProperty("db.password");
            }
            case APP_NAME: {
                return _properties.getProperty("app.name");
            }
            case APP_IMAGES_PATH: {
                return _properties.getProperty("app.images_path");
            }
        }

        return null;
    }

    private void LoadData() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(_propFilePath);

        try {
            if (inputStream != null) {
                _properties.load(inputStream);
            } else {
                throw new FileNotFoundException("Property file " + _propFilePath + " not found in the classpath");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
