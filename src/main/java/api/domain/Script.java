package api.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by i303874 on 12/23/14.
 */
public class Script extends service.domain.Script {

    public Script(service.domain.Script script) {
        this(script.getId(), script.getData());
    }

    @JsonCreator
    public Script(@JsonProperty("id") String id, @JsonProperty("data") String data) {
        super(id, data);
    }
}
