package com.aliniaz.tickettriage.ticket.analysis.review.dto;

import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;

public record TicketReviewDecision(
        ReviewStatus reviewStatus,
        String reviewReason
) {
}