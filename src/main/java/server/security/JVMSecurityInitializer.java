package server.security;

/**
 * Created by i303874 on 12/28/14.
 */
public class JVMSecurityInitializer {
    public void initialize() {
        java.security.Policy.setPolicy(new JVMSecurityPolicy());
        System.setSecurityManager(new SecurityManager());
    }
}
