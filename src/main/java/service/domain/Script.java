package service.domain;

/**
 * Created by i303874 on 12/23/14.
 */
public class Script {
    private String id;

    private String data;

//    private groovy.lang.Script groovyScript;

    public Script(Script script) {
        this.id = script.getId();
        this.data = script.getData();
//        this.groovyScript = script.getGroovyScript();
    }

    public Script(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public Script(Script script, String id) {
        this(script);
        this.id = id;
    }

//    public Script(Script script, groovy.lang.Script groovyScript) {
//        this(script);
//        this.groovyScript = groovyScript;
//    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

//    public groovy.lang.Script getGroovyScript() {
//        return groovyScript;
//    }
}
