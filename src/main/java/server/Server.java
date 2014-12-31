package server;

import api.ScriptAPI;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import script.rhino.RhinoScriptRunner;
import server.security.JVMSecurityInitializer;
import service.ScriptService;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by i303874 on 12/23/14.
 */
public class Server {

    private final static Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;

    public Server(int port) {
        checkArgument(port > 0 && port <= 65535);
        this.port = port;
    }

    public void run() throws ServerException {
        new JVMSecurityInitializer().initialize();

        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS | ServletContextHandler.NO_SECURITY);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new ServletContainer(resourceConfig())), "/*");

        try {
            logger.info("running webserver on port " + port);
            server.start();
            server.join();
        } catch (Exception e) {
            logger.error("execution of webserver failed", e);
            throw new ServerException(e);
        } finally {
            if (server != null) {
                server.destroy();
            }
        }
    }

    private static ResourceConfig resourceConfig() {
        try {
            ScriptService scriptService = new ScriptService(new RhinoScriptRunner());

            return new ResourceConfig().register(new ScriptAPI(scriptService));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
