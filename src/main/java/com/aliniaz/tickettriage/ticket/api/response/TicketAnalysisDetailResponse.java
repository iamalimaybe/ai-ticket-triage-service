package com.aliniaz.tickettriage.ticket.api.response;

import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;

import java.time.Instant;
import java.util.List;

public record TicketAnalysisDetailResponse(
        Long analysisId,
        String subject,
        String body,
        String customerId,
        String analysisSource,
        String rawModelOutput,
        Double modelConfidence,
        ReviewStatus reviewStatus,
        String reviewReason,
        Instant reviewedAt,
        String reviewedBy,
        AnalysisStatus status,
        TicketCategory category,
        TicketPriority priority,
        String customerIntent,
        List<String> missingInformation,
        List<String> validationMessages,
        Instant createdAt,
        Instant updatedAt
) {
}