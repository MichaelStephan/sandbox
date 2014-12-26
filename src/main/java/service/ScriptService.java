package service;

import com.google.common.util.concurrent.*;
import common.domain.PageableParameter;
import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import service.domain.Script;
import service.security.CompiletimeSecurity;

import java.security.*;
import java.security.cert.Certificate;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by i303874 on 12/23/14.
 */
public class ScriptService {
    private final static int MAX_PAGE_SIZE = 1000;

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    private TreeMap<String, Script> scripts = new TreeMap<>();

    private final GroovyShell scriptShell;

    private AccessControlContext scriptContext;

    public ScriptService() {
        scriptShell = new GroovyShell(new CompilerConfiguration().addCompilationCustomizers(new CompiletimeSecurity()));
        Permissions perms = new Permissions();
        ProtectionDomain[] domains = new ProtectionDomain[]{new ProtectionDomain(new CodeSource(null, (Certificate[]) null), perms)};
        scriptContext = new AccessControlContext(domains);
    }

    public ListenableFuture<Object> runScript(String id) {
        return Futures.transform(getScript(id), (AsyncFunction<Script, Object>) (Script script) ->
                executor.submit(() -> {
                            return AccessController.doPrivileged(new PrivilegedAction() {
                                public Object run() {
                                    return script.getGroovyScript().run();
                                }
                            }, scriptContext);
                        }
                ));
    }

    public ListenableFuture<Script> getScript(String id) {
        return executor.submit(() -> {
            Script script = scripts.get(id);
            if (script == null) {
                throw new NoSuchElementException(id + " does not exist");
            } else {
                return script;
            }
        });
    }

    public ListenableFuture<List<Script>> listScripts(PageableParameter pageableParameter) {
        return executor.submit(() -> {
            if (pageableParameter == null || pageableParameter.getPage() < 1 || pageableParameter.getPageSize() < 1 || pageableParameter.getPageSize() > MAX_PAGE_SIZE) {
                throw new IllegalArgumentException("invalid pageable parameters");
            }

            List<Script> result = new ArrayList<>(pageableParameter.getPageSize());

            int read = 0;
            int start = (pageableParameter.getPage() - 1) * pageableParameter.getPageSize();
            for (Iterator<Script> iscripts = scripts.values().iterator(); result.size() < pageableParameter.getPageSize() && iscripts.hasNext(); read++) {
                Script script = iscripts.next();
                if (read >= start) {
                    result.add(script);
                }
            }

            return result;
        });
    }

    public ListenableFuture<Script> newScript(Script script) {
        return executor.submit(() -> {
            Script newScript = script;
            if (script.getId() == null) {
                newScript = new Script(script, UUID.randomUUID().toString());
            }

            scripts.put(newScript.getId(), new Script(newScript, compileScript(newScript)));
            return newScript;
        });
    }

    private groovy.lang.Script compileScript(Script script) {
        Binding binding = new Binding();
        groovy.lang.Script groovyScript = scriptShell.parse(new GroovyCodeSource(script.getData(), "untrusted", "/untrusted"));
        groovyScript.setBinding(binding);
        return groovyScript;
    }
}
