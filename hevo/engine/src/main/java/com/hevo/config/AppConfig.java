package com.hevo.config;

import com.hevo.elasticsearch.ESClient;
import com.hevo.elasticsearch.ElasticSearchConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ElasticSearchConfiguration elasticSearchConfiguration() {
        return ElasticSearchConfiguration.builder()
                .name("android")

                .scheme("https")
                .port(9200)
                .username("elastic")
                
                .readThreadPoolSize(1)
                .writeThreadPoolSize(1)
                .bulkWriteThreadPoolSize(1)
                .timeOutInMillis(1)
                .env("gcp")
                .build();
    }

    @Bean
    public ESClient esClient(ElasticSearchConfiguration esconfig) {
        return ESClient.getInstance(esconfig);
    }

}
