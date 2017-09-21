package utils;

import com.amazonaws.services.cognitosync.model.InvalidConfigurationException;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by dshelygin on 13.09.2017.
 * Обеспечивает работу с конфигурационным файлом приложения
 */
public enum Configuration {
    INSTANCE;

    private Properties props = new Properties();

    //имя поля в конфигурационном файле, содержащее access key
    private static final String ACCESS_KEY_NAME = "accessKey";
    private static final String SECRET_KEY_NAME = "secretKey";


    private Configuration() {
        if (props.isEmpty()) {
            File configFile = new File("config.xml");
            try {
                InputStream inputStream = new FileInputStream(configFile);
                props.loadFromXML(inputStream);
            } catch (Exception e) {
                Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, e.toString());
            }
        }
    }


    private Properties getProperties() {
        return  props;
    }

    public String getAccessKey() throws InvalidConfigurationException {
        String accessKey = getProperties().getProperty(ACCESS_KEY_NAME);
        if (accessKey == null ) {
            Logger.getLogger(Configuration.class.getName())
                    .log(Level.SEVERE, ACCESS_KEY_NAME + " not found in config.xml");
            System.exit(1);
        }
            return accessKey;
    }

    public String getSecretKey() {
        String secretKey = getProperties().getProperty(SECRET_KEY_NAME);
        if (secretKey == null ) {
            Logger.getLogger(Configuration.class.getName())
                    .log(Level.SEVERE, ACCESS_KEY_NAME + " not found in config.xml");
            System.exit(1);
        }

        return secretKey;

    }
}
