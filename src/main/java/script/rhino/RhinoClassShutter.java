package script.rhino;

import org.mozilla.javascript.ClassShutter;

/**
 * A {@link ClassShutter} that locks out access to all native classes.
 */
public class RhinoClassShutter implements ClassShutter {
    public boolean visibleToScripts(String fullClassName) {
        return false;
    }
}