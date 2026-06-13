package com.aliniaz.tickettriage.ticket.api.response;

import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;

import java.time.Instant;

public record TicketAnalysisSummaryResponse(
        Long analysisId,
        String subject,
        String customerId,
        String analysisSource,
        Double modelConfidence,
        ReviewStatus reviewStatus,
        String reviewReason,
        AnalysisStatus status,
        TicketCategory category,
        TicketPriority priority,
        Instant createdAt,
        Instant updatedAt
) {
}