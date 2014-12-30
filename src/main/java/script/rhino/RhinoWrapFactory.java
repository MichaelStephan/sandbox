package script.rhino;

/**
 * Created by i303874 on 12/26/14.
 */
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * A {@link WrapFactory} that ensures {@link org.mozilla.javascript.NativeJavaObject} instances are of the
 * {@link RhinoNativeJavaObject} variety.
 */
public class RhinoWrapFactory extends WrapFactory {

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
        return new RhinoNativeJavaObject(scope, javaObject, staticType);
    }
}