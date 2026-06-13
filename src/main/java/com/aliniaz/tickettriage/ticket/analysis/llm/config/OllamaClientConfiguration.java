package com.aliniaz.tickettriage.ticket.analysis.llm.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(OllamaProperties.class)
public class OllamaClientConfiguration {

    @Bean
    @Qualifier("ollamaRestClient")
    public RestClient ollamaRestClient(RestClient.Builder builder, OllamaProperties properties) {
        return builder
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}