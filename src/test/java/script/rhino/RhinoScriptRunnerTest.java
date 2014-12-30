package script.rhino;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import service.domain.Script;

import java.io.File;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

/**
 * Created by i303874 on 12/30/14.
 */
public class RhinoScriptRunnerTest {

    private Map<String, Object> convertResultToMap(Object result) {
        assertTrue(result instanceof Map);
        return Map.class.cast(result);
    }

    private List<Object> convertResultToList(Object result) {
        assertTrue(result instanceof List);
        return List.class.cast(result);
    }

    @Test
    public void testRunWithSimpleJsonResult() throws Exception {
        RhinoScriptRunner runner = new RhinoScriptRunner();
        Object result = runner.run(new Script("test", FileUtils.readFileToString(new File("./src/test/resources/script/rhino/RhinoScriptRunnerTest_testRunWithSimpleJsonResult.js"))));
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultAsMap = convertResultToMap(result);
        assertTrue(resultAsMap.containsKey("a"));
        assertEquals("b", resultAsMap.get("a"));
    }

    @Test
    public void testRunWithNestedJsonResult() throws Exception {
        RhinoScriptRunner runner = new RhinoScriptRunner();
        Object result = runner.run(new Script("test", FileUtils.readFileToString(new File("./src/test/resources/script/rhino/RhinoScriptRunnerTest_testRunWithNestedJsonResult.js"))));
        System.out.println(result);
        assertNotNull(result);
        assertTrue(result instanceof Map);
        Map<String, Object> resultAsMap = convertResultToMap(result);
        assertTrue(resultAsMap.containsKey("a"));

        Map<String, Object> resultAsMap2 = convertResultToMap(resultAsMap.get("a"));
        assertEquals(1.0, resultAsMap2.get("b"));
        assertEquals(1.0, resultAsMap2.get("c"));
        assertEquals("e", resultAsMap2.get("d"));

        List<Object> resultAsList = convertResultToList(resultAsMap2.get("f"));
        assertEquals(1.0, resultAsList.get(0));
        assertEquals(1.0, resultAsList.get(1));
        assertEquals("e", resultAsList.get(2));
        assertEquals(false, resultAsList.get(3));

        assertEquals(true, resultAsMap2.get("g"));
    }

    @Test
    public void testRunWithInfiniteJsonResult() throws Exception {
        RhinoScriptRunner runner = new RhinoScriptRunner();
        Object result = runner.run(new Script("test", FileUtils.readFileToString(new File("./src/test/resources/script/rhino/RhinoScriptRunnerTest_testRunWithInfiniteJsonResult.js"))));

    }
}
