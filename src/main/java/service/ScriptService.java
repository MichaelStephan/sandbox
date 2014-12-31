package service;

import com.google.common.util.concurrent.*;
import common.domain.PageableParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import script.ScriptException;
import script.ScriptRunner;
import service.domain.Script;

import java.util.*;
import java.util.concurrent.Executors;

/**
 * Created by i303874 on 12/23/14.
 */
public class ScriptService {
    private final static Logger logger = LoggerFactory.getLogger(ScriptService.class);

    private final static int MAX_PAGE_SIZE = 1000;

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));

    private TreeMap<String, Script> scripts = new TreeMap<>();

    private final ScriptRunner scriptRunner;

    public ScriptService(ScriptRunner scriptRunner) throws ScriptException {
        this.scriptRunner = scriptRunner;
    }

    public ListenableFuture<Object> runScript(String id) {
        return Futures.transform(getScript(id), (AsyncFunction<Script, Object>) (Script script) ->
                executor.submit(() -> {
                            return scriptRunner.run(script);
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

            scripts.put(newScript.getId(), newScript);
            return newScript;
        });
    }
}
