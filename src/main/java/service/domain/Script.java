package service.domain;

/**
 * Created by i303874 on 12/23/14.
 */
public class Script {
    private String id;

    private String data;

    public Script(Script script) {
        this.id = script.getId();
        this.data = script.getData();
    }

    public Script(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public Script(Script script, String id) {
        this(script);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }
}
