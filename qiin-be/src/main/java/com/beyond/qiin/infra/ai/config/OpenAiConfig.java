package com.beyond.qiin.infra.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenAiConfig {

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.api-key}")
    private String apiKey;

    @Bean
    public RestClient openAiRestClient() {
        return RestClient.builder()
                         .baseUrl(baseUrl)
                         .defaultHeader("Authorization", "Bearer " + apiKey)
                         .defaultHeader("Content-Type", "application/json")
                         .build();
    }
}
