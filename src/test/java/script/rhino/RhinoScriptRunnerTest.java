package script.rhino;

import com.google.common.base.Stopwatch;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import script.ScriptException;
import service.domain.Script;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        throw new NotImplementedException();
    }

    @Test(expected = ScriptException.class)
    public void testRunWithLongRunningScript() throws Exception {
        new RhinoScriptRunner().run(new Script("test", "while(true);"));
    }

    @Test
    public void testRunWithPrimeScriptedVsPrimeNativeComparison() throws Exception {
        int maxExecutions = 500;
        long maxPrimes = 100000;

        String rawScript = "" +
                "function getPrimes(max) {\n" +
                "    var sieve = [], i, j, primes = [];\n" +
                "    for (i = 2; i <= max; ++i) {\n" +
                "        if (!sieve[i]) {\n" +
                "            // i has not been marked -- it is prime\n" +
                "            primes.push(i);\n" +
                "            for (j = i << 1; j <= max; j += i) {\n" +
                "                sieve[j] = true;\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "    return primes;\n" +
                "}\n" +
                "\n" +
                "getPrimes(" + maxPrimes + ");";

        RhinoScriptRunner runner = new RhinoScriptRunner();
        Script script = new Script("test", rawScript);

        Stopwatch scriptStopWatch = Stopwatch.createStarted();
        for (int i = 0; i < maxExecutions; i++) {
            runner.run(script);
        }
        scriptStopWatch.stop();

        Stopwatch nativeStopWatch = Stopwatch.createStarted();
        for (int i = 0; i < maxExecutions; i++) {
            getPrimes(maxPrimes);
        }
        nativeStopWatch.stop();

        System.out.println("script: " + scriptStopWatch.elapsed(TimeUnit.MILLISECONDS) + " vs. native: " + nativeStopWatch.elapsed(TimeUnit.MILLISECONDS));

        // script is assumed to be approx. 10 times slower than the native implementation
        assertTrue((scriptStopWatch.elapsed(TimeUnit.MILLISECONDS) / nativeStopWatch.elapsed(TimeUnit.MILLISECONDS)) <= 10.0f);
    }

    private List<Number> getPrimes(long max) {
        Map<Long, Object> sieve = new HashMap<>();
        List<Number> primes = new LinkedList<>();
        long i, j;

        for (i = 2; i <= max; ++i) {
            if (!sieve.containsKey(i)) {
                // i has not been marked -- it is prime
                primes.add(i);
                for (j = i << 1; j <= max; j += i) {
                    sieve.put(j, null);
                }
            }
        }
        return primes;
    }
}
