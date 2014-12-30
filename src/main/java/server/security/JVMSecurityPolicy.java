package server.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by i303874 on 12/25/14.
 */
public final class JVMSecurityPolicy extends java.security.Policy {
    private final Set<URL> locations;

    public JVMSecurityPolicy() {
        try {
            locations = new HashSet<URL>();
            locations.add(new URL("file", "", "/untrusted"));
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = new Permissions();
        if (!locations.contains(codesource.getLocation())) {
            perms.add(new AllPermission());
        }
        return perms;
    }
}