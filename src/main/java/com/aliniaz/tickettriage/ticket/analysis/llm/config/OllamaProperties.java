package com.aliniaz.tickettriage.ticket.analysis.llm.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ticket.triage.ollama")
public class OllamaProperties {

    private String baseUrl = "http://localhost:11434";

    private String model = "qwen3:4b";
}