import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mozilla.javascript.*;
import sandbox.SandboxContextFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by i303874 on 12/26/14.
 */
public class RhinoTest {

    private static Scriptable scope;

//    public static final void invokeFooServiceAsync(String arg) {
//        Context cx = getContext();
//        ContinuationPending pending = cx.captureContinuation();
//        // Script is paused here
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        ScriptableOutputStream sos = new ScriptableOutputStream(baos, getScope());
//        sos.writeObject(pending.getContinuation());
//        sos.writeObject(getScope());
//        String servicePayload = Base64.encodeBase64String(baos.toByteArray());
//        invokeFooServiceForReal(arg, servicePayload); // This method invokes the async service
//    }
//
//    public static final void returnFooServiceResponse(FooResponse response, String servicePayload) {
//        // De serialize the state of script
//        byte[] continuationAndScope = Base64.decodeBase64(servicePayload);
//        ScriptableInputStream sis = new ScriptableInputStream(new ByteArrayInputStream(continuationAndScope), getScope());
//        Scriptable continuation = (Scriptable) sis.readObject();
//        Scriptable scope = (Scriptable) sis.readObject();
//        getContext().resumeContinuation(continuation, scope, response);
//        // Script resumed
//    }

    public static void someHeavyProcessing(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        ContinuationPending pending = cx.captureContinuation();

        Object lock = new Object();
        synchronized (lock) {
            try {
                System.out.println("going to sleep");
                lock.wait(200);
                System.out.println("awaking again");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        cx.resumeContinuation(pending.getContinuation(), scope, "lulu");
    }

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

    @Test
    public void Test() {
        SandboxContextFactory contextFactory = new SandboxContextFactory(2, TimeUnit.SECONDS);
        ContextFactory.initGlobal(contextFactory);

        Context context = contextFactory.makeContext();
        context.setOptimizationLevel(-1);
        contextFactory.enterContext(context);
        try {
            ScriptableObject prototype = context.initStandardObjects();
            prototype.setParentScope(null);
            prototype.defineFunctionProperties(new String[]{"println", "someHeavyProcessing"}, getClass(), ScriptableObject.DONTENUM);
            scope = context.newObject(prototype);
            scope.setPrototype(prototype);
//            context.evaluateString(scope, "while(true){ println('lala') };", null, -1, null);

            try {
                Script script = context.compileString(FileUtils.readFileToString(new File("./src/test/resources/test.js")), null, -1, null);
                NativeObject result = (NativeObject)context.executeScriptWithContinuations(script, scope);

                Object[] propIds = NativeObject.getPropertyIds(result);
                for(Object propId: propIds) {
                    String key = propId.toString();
                    String value = NativeObject.getProperty(result, key).toString();
                    System.out.println(value);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // do what you want within the scope
        } finally {
            Context.exit();
        }
    }
}
