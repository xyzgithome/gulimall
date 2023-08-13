package com.atguigu.gulimall.search.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.HttpAsyncResponseConsumerFactory;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Configuration
public class ElasticSearchConfig {
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }


    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("192.168.56.10", 9200, "http"))
//                        .setHttpClientConfigCallback(
//                                httpAsyncClientBuilder -> {
//                                    httpAsyncClientBuilder.setKeepAliveStrategy(
//                                            ((httpResponse, httpContext) -> Duration.ofMinutes(3).toMillis()));
//                                    httpAsyncClientBuilder.disableAuthCaching();
//                                    return httpAsyncClientBuilder;
//                                })
        );
    }

}