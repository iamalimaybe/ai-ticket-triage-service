package com.aliniaz.tickettriage.ticket.api.request;

import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketAnalysisReviewRequest(
        @NotNull
        ReviewStatus reviewStatus,

        @Size(max = 500)
        String reviewReason,

        @Size(max = 120)
        String reviewedBy
) {
}