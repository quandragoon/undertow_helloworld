package helloworld.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Configuration, filled from yml config files
 */
public class Config {

    private static final Log logger = LogFactory.getLog(Config.class);
    private static Config instance = null; // singleton instance

    public int port;
    public int httpPort = 4080;
    public String keyStoreFile;
    public String hostname = "";

    private Config() {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            logger.warn("event=get_localhost_hostname class=Config func=constructor status=failed exception=" + e.getMessage());
        }
    }

    public int getPort() {
        return port;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public String getHostName() {
        return hostname;
    }

    public static Config getInstance() {
        return instance;
    }

    public int getHttpPort(){
        return httpPort;
    }
    /**
     * Loads the InputStream into the singleton instance.
     *
     * @param resource
     * @return
     * @throws IOException
     */
    public static void init(InputStream resource) throws IOException {
        if (resource == null) throw new NullPointerException();
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    logger.info("Initializing Config");
                    instance = (Config) new Yaml(new Constructor(Config.class)).load(resource);
                }
            }
        }
    }
}