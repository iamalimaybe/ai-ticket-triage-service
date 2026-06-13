package com.aliniaz.tickettriage.ticket.analysis.dto;

import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;

import java.util.List;

public record TicketTriageAnalysis(
        String analysisSource,
        AnalysisStatus status,
        TicketCategory category,
        TicketPriority priority,
        String customerIntent,
        List<String> missingInformation,
        List<String> validationMessages
) {
}