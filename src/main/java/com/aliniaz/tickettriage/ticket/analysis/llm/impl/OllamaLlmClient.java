package com.aliniaz.tickettriage.ticket.analysis.llm.impl;

import com.aliniaz.tickettriage.ticket.analysis.llm.LlmClient;
import com.aliniaz.tickettriage.ticket.analysis.llm.config.OllamaProperties;
import com.aliniaz.tickettriage.ticket.analysis.llm.dto.OllamaGenerateRequest;
import com.aliniaz.tickettriage.ticket.analysis.llm.dto.OllamaGenerateResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.isBlank;

@Component
public class OllamaLlmClient implements LlmClient {

    private final RestClient ollamaRestClient;
    private final OllamaProperties ollamaProperties;

    public OllamaLlmClient(
            @Qualifier("ollamaRestClient") RestClient ollamaRestClient,
            OllamaProperties ollamaProperties
    ) {
        this.ollamaRestClient = ollamaRestClient;
        this.ollamaProperties = ollamaProperties;
    }

    @Override
    public String generate(String prompt) {
        OllamaGenerateRequest request = new OllamaGenerateRequest(
                ollamaProperties.getModel(),
                prompt,
                false
        );

        OllamaGenerateResponse response = ollamaRestClient.post()
                .uri("/api/generate")
                .body(request)
                .retrieve()
                .body(OllamaGenerateResponse.class);

        if (response == null || isBlank(response.response())) {
            throw new IllegalStateException("Ollama returned an empty response.");
        }

        return response.response();
    }
}