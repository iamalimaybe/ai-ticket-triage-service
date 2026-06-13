package com.aliniaz.tickettriage.ticket.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ticketTriageOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Ticket Triage Service API")
                        .version("0.1.0")
                        .description("""
                                Backend API for analyzing support tickets with deterministic or local LLM-based analysis.

                                LLM output is treated as untrusted until parsed, validated, persisted, and reviewed when needed.
                                """));
    }
}