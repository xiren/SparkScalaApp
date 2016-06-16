import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigProperties {
    private static final Logger log = LoggerFactory.getLogger(ConfigProperties.class);

    private static Properties properties = new Properties();

    static {
        File file = new File(ConfigProperties.class.getClassLoader().getResource("config.properties").getFile());
        InputStream is = null;
        try {
            
            is = new FileInputStream(file);
            properties.load(is);

            log.info("config.properties update:");
            for (Object key : properties.keySet()) {
                if (!((String) key).startsWith("init")) {
                    log.info("  " + key + "=" + properties.getProperty((String) key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            log.error("ConfigProperties CheckThread interrupted");
        }
    }

    public static String getProperty(String name) {
        return properties.getProperty(name);
    }

}
