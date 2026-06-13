package com.aliniaz.tickettriage.ticket.analysis.review.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.review.TicketReviewDecisionService;
import com.aliniaz.tickettriage.ticket.analysis.review.config.TicketReviewProperties;
import com.aliniaz.tickettriage.ticket.analysis.review.dto.TicketReviewDecision;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultTicketReviewDecisionService implements TicketReviewDecisionService {

    private final TicketReviewProperties ticketReviewProperties;

    @Override
    public TicketReviewDecision decide(TicketTriageAnalysis analysis) {
        if (analysis.status() == AnalysisStatus.FAILED) {
            return new TicketReviewDecision(
                    ReviewStatus.NEEDS_REVIEW,
                    "Analysis failed validation."
            );
        }

        if (analysis.modelConfidence() != null
                && analysis.modelConfidence() < ticketReviewProperties.getConfidenceThreshold()) {
            return new TicketReviewDecision(
                    ReviewStatus.NEEDS_REVIEW,
                    "Model confidence is below review threshold."
            );
        }

        return new TicketReviewDecision(
                ReviewStatus.NOT_REQUIRED,
                null
        );
    }
}