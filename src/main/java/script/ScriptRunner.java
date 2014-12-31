package script;

import service.domain.Script;

/**
 * Created by i303874 on 12/31/14.
 */
public interface ScriptRunner {
    Object run(Script script) throws ScriptException;
}
