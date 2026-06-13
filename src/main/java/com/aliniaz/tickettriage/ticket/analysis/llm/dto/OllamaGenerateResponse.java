package com.aliniaz.tickettriage.ticket.analysis.llm.dto;

public record OllamaGenerateResponse(
        String response,
        Boolean done
) {
}