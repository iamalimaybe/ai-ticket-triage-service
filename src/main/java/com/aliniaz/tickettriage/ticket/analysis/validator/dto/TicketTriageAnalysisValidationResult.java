package com.aliniaz.tickettriage.ticket.analysis.validator.dto;

import java.util.List;

public record TicketTriageAnalysisValidationResult(
        boolean valid,
        List<String> messages
) {
}
