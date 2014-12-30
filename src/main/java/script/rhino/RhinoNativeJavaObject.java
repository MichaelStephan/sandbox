package script.rhino;

/**
 * Created by i303874 on 12/26/14.
 */

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * A sandboxed {@link NativeJavaObject} that prevents using reflection to escape a sandbox.
 */
public class RhinoNativeJavaObject extends NativeJavaObject {

    public RhinoNativeJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    @Override
    public Object get(String name, Scriptable start) {
        if ("getClass".equals(name)) {
            return NOT_FOUND;
        } else if ("class".equals(name)) {
            return NOT_FOUND;
        }
        return super.get(name, start);
    }
}