package com.hevo.elasticsearch;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class EsIndexRequest {
    private String index;
    private Object source;
    private String routing;

    @Setter
    private String id;

    @Builder
    public EsIndexRequest(String index, Object source, String id, String routing) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(source);
        this.index = index;
        this.source = source;
        this.id = id;
        this.routing = routing;
    }
}
