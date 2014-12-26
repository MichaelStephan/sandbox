package common.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by i303874 on 12/23/14.
 */
public class PageableParameter {
    private int page;

    private int pageSize;

    @JsonCreator
    public PageableParameter(@JsonProperty("page") int page, @JsonProperty("pageSize") int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPage() {
        return page;
    }
}
