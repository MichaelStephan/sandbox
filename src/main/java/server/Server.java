package server;

import api.ScriptAPI;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import service.ScriptService;

/**
 * Created by i303874 on 12/23/14.
 */
public class Server {
    private final static int DEVPORT = 8080;

    private final static int ERRORCODE = 200;

    private static boolean devMode = false;

    private final static boolean isDevMode(String[] args) {
        for (String arg : args) {
            if ("-dev".equalsIgnoreCase(arg.trim())) {
                System.out.println("dev mode enabled");
                return true;
            }
        }
        return false;
    }

    private final static void bye(int code) {
        System.err.println("saying good bye due to error");
        System.exit(code);
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
        devMode = isDevMode(args);

        int webServerPort = -1;
        try {
            webServerPort = getPort();
        } catch (Exception e) {
            if (devMode) {
                System.out.println("setting dev mode port " + DEVPORT);
                webServerPort = DEVPORT;
            } else {
                System.err.println(e.getMessage());
                bye(ERRORCODE);
            }
        }

        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(webServerPort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new ServletContainer(resourceConfig())), "/*");

        try {
            System.out.println("running webserver on port " + webServerPort);
            server.start();
            server.dump();
            server.dumpStdErr();
            server.join();
        } catch (Exception e) {
            System.err.println("execution of webserver failed - " + e.getMessage());
            bye(ERRORCODE);
        } finally {
            if (server != null) {
                server.destroy();
            }
        }
    }

    private static ResourceConfig resourceConfig() {
        ScriptService scriptService = new ScriptService();

        return new ResourceConfig().register(new ScriptAPI(scriptService));
    }
}
