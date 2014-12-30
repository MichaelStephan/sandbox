package script.rhino;

import org.mozilla.javascript.*;
import script.ScriptException;
import service.domain.Script;

import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by i303874 on 12/28/14.
 */
public class RhinoScriptRunner {
    private final static int MAX_SCRIPT_RUNTIME = 5;

    private final RhinoContextFactory contextFactory;

    private final CodeSource codeSource;

    public static Object println(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                System.out.print(" ");
            }

            // Convert the arbitrary JavaScript value into a string form.
            String s = Context.toString(args[i]);
            System.out.print(s);
        }
        System.out.println();
        return Context.getUndefinedValue();
    }

    public RhinoScriptRunner() throws ScriptException {
        try {
            codeSource = new CodeSource(new URL("file:/untrusted"), (Certificate[]) null);
        } catch (Exception e) {
            throw new ScriptException("unable to initialize script runner. Error occurred during loading code source", e);
        }

        if (!SecurityController.hasGlobal()) {
            SecurityController.initGlobal(new PolicySecurityController());
        }

        contextFactory = new RhinoContextFactory(MAX_SCRIPT_RUNTIME, TimeUnit.SECONDS);
        if (!RhinoContextFactory.hasExplicitGlobal()) {
            ContextFactory.initGlobal(contextFactory);
        }
    }

    private Object convertNativeToJava(Context context, Object object) {
        if (object instanceof NativeObject) {
            return convertNativeObjectToHashMap(context, (NativeObject) object);
        } else if (object instanceof NativeArray) {
            return convertNativeArrayToList(context, (NativeArray) object);
        } else if (object instanceof String) {
            return context.jsToJava(object, String.class);
        } else if (object instanceof Double) {
            return context.jsToJava(object, Number.class);
        } else if (object instanceof Boolean) {
            return context.jsToJava(object, Boolean.class);
        } else {
            return null;
        }
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value");
        }
        return (int) l;
    }

    private List<Object> convertNativeArrayToList(Context context, NativeArray array) {
        if (array == null || array.getLength() < 1) {
            return Collections.emptyList();
        }

        List<Object> list = new ArrayList<Object>(safeLongToInt(array.getLength()));
        for (Object id : array.getIds()) {
            list.add(convertNativeToJava(context, array.get((int) id, (Scriptable) null)));
        }
        return list;
    }

    private Map<String, Object> convertNativeObjectToHashMap(Context context, NativeObject object) {
        if (object == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>();
        Object[] propIds = NativeObject.getPropertyIds(object);
        for (Object propId : propIds) {
            String key = propId.toString();
            map.put(key, convertNativeToJava(context, NativeObject.getProperty(object, key)));
        }

        return map;
    }

    public Object run(Script script) throws ScriptException {
        Context context = contextFactory.makeContext();
        contextFactory.enterContext(context);
        try {
            ScriptableObject prototype = context.initStandardObjects();
            prototype.setParentScope(null);
            prototype.defineFunctionProperties(new String[]{"println"}, getClass(), ScriptableObject.DONTENUM);
            Scriptable scope = context.newObject(prototype);
            scope.setPrototype(prototype);

            org.mozilla.javascript.Script jsScript = context.compileString(script.getData(), null, -1, codeSource);
            return convertNativeToJava(context, context.executeScriptWithContinuations(jsScript, scope));
        } catch (Exception e) {
            throw new ScriptException("failed to execute script", e);
        } finally {
            Context.exit();
        }
    }
}
