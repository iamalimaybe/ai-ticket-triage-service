package com.aliniaz.tickettriage.ticket.analysis.parser.dto;

import java.util.List;

public record LlmTicketTriageOutput(
        String category,
        String priority,
        String customerIntent,
        List<String> missingInformation,
        Double confidence
) {
}