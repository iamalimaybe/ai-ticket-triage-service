package com.aliniaz.tickettriage.ticket.analysis.llm.dto;

public record OllamaGenerateRequest(
        String model,
        String prompt,
        boolean stream
) {
}