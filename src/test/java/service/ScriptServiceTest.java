package service;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import server.security.JVMSecurityInitializer;
import service.domain.Script;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Created by i303874 on 12/23/14.
 */
public class ScriptServiceTest {
    @Test
    public void testRunScript() throws Exception {
        new JVMSecurityInitializer().initialize();

        ScriptService scriptService = new ScriptService();

        scriptService.newScript(new Script("test", FileUtils.readFileToString(new File("./src/test/resources/test.groovy"), StandardCharsets.UTF_8.name())))
                .get(10, TimeUnit.SECONDS);

        try {
            scriptService.runScript("test").get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
