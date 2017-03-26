package helloworld.rest;

import com.google.common.io.Resources;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.util.Headers;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xnio.Options;
import org.xnio.Sequence;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;

public class UndertowServer {

    private static final Log log = LogFactory.getLog(UndertowServer.class);

    private static Undertow instance;
    private static Config c;
    private static final int MAX_CONCURRENT_REQUESTS = 1000;
    private static final int REQUESTS_QUEUE_SIZE = 500;

    public static void startUndertow() throws Exception {
        c = Config.getInstance();

        /*
        KeyStore ks = KeyStore.getInstance("JKS");
        InputStream ksInput;

        if (c.getKeyStoreFile() != null && new File(c.getKeyStoreFile()).exists()) {
            ksInput = new FileInputStream(c.getKeyStoreFile());
        } else {
            ksInput = Resources.getResource(c.getKeyStoreFile()).openStream();
        }

        ks.load(ksInput, "helloworld".toCharArray());
        KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        factory.init(ks, "helloworld".toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(factory.getKeyManagers(), null, SecureRandom.getInstance("SHA1PRNG"));
        */

        // Undertow.Builder builder = Undertow.builder().addHttpsListener(c.getPort(), "0.0.0.0", SSLContext.getDefault())
        Undertow.Builder builder = Undertow.builder().addHttpListener(c.getPort(), "0.0.0.0")
                .addHttpListener(c.getHttpPort(), "0.0.0.0")
                .setBufferSize(1024 * 16)
                .setIoThreads(Math.max(1, Runtime.getRuntime().availableProcessors() - 1))
                .setSocketOption(Options.SSL_ENABLED_CIPHER_SUITES, Sequence.of(
                        "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
                        "TLS_RSA_WITH_AES_128_CBC_SHA",
                        "TLS_DHE_RSA_WITH_AES_128_CBC_SHA"))
                .setSocketOption(Options.BACKLOG, 20)
                .setServerOption(UndertowOptions.ALWAYS_SET_DATE, true)
                .setWorkerOption(Options.CONNECTION_HIGH_WATER, 100000)
                .setWorkerOption(Options.CONNECTION_LOW_WATER, 60000)
                .setHandler(new RequestLimitingHandler(MAX_CONCURRENT_REQUESTS, REQUESTS_QUEUE_SIZE,
                        Handlers.header(Handlers.path().addPrefixPath("/", new Handler()),
                                Headers.SERVER_STRING, "Hello World")))
                .setWorkerThreads(1000);
        instance = builder.build();
        instance.start();
        log.info("Listening in port " + c.getPort());
    }

    public static void main(String[] args) throws Exception {
        InputStream is = null;
        try {
            is = Resources.getResource("config.yml").openStream();
            Config.init(is);
            log.info("event=load_config class=Server func=main input=" + Resources.getResource("config.yml"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        startUndertow();
    }

    /**
     * Not thread safe.
     */
    public static void stop() {
        if (instance != null) {
            instance.stop();
            instance = null;
        }
    }

    public static boolean isRunning() {
        return instance != null;
    }
}
