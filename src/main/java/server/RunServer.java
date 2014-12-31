package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by i303874 on 12/31/14.
 */
public class RunServer {
    private final static Logger logger = LoggerFactory.getLogger(RunServer.class);

    private final static int DEVPORT = 8080;

    private final static int ERRORCODE = 200;

    private final static boolean isDevMode(String[] args) {
        for (String arg : args) {
            if ("-dev".equalsIgnoreCase(arg.trim())) {
                logger.info("dev mode enabled");
                return true;
            }
        }
        return false;
    }

    private final static void bye(Exception e) {
        logger.error("saying good bye due to error", e);
        System.exit(ERRORCODE);
    }

    private final static int getPort() throws Exception {
        String portStr = null;
        try {
            portStr = System.getenv("PORT");
            if (portStr.trim().equals("")) {
                throw new Exception("environment variable PORT is empty");
            }
        } catch (NullPointerException e) {
            throw new Exception("environment variable PORT is not set");
        }

        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("environment variable PORT could not be parse into integer");
        }
    }

    public static void main(String[] args) {
        boolean devMode = isDevMode(args);

        int webServerPort = -1;
        try {
            webServerPort = getPort();
        } catch (Exception e) {
            if (devMode) {
                logger.info("setting dev mode port " + DEVPORT);
                webServerPort = DEVPORT;
            } else {
                bye(e);
            }
        }

        Server server = new Server(webServerPort);
        try {
            server.run();
        } catch (ServerException e) {
            e.printStackTrace();
        }
    }
}
