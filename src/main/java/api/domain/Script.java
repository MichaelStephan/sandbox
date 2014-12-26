package api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by i303874 on 12/23/14.
 */
public class Script {
    private String id;

    private String data;

    public Script(service.domain.Script script) {
        this(script.getId(), script.getData());
    }

    @JsonCreator
    public Script(@JsonProperty("id") String id, @JsonProperty("data") String data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public service.domain.Script asInternalScript() {
        return new service.domain.Script(id, data);
    }
}
