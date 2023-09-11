package com.hevo.elasticsearch;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ElasticSearchConfiguration implements Serializable {

    // todo: create a config map
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("scheme")
    private String scheme;

    @JsonProperty("port")
    private int port;

    @JsonProperty("username")
    private String username;

    @JsonProperty("password")
    private String password;

    private int readThreadPoolSize;
    private int writeThreadPoolSize;
    private int bulkWriteThreadPoolSize;

    private long timeOutInMillis;

    private String env;

}