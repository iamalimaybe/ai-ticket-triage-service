package com.aliniaz.tickettriage.ticket.analysis.review.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.review.config.TicketReviewProperties;
import com.aliniaz.tickettriage.ticket.analysis.review.dto.TicketReviewDecision;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTicketReviewDecisionServiceTest {

    @Test
    void decideRequiresReviewWhenAnalysisFailed() {
        TicketReviewProperties properties = new TicketReviewProperties();
        DefaultTicketReviewDecisionService service = new DefaultTicketReviewDecisionService(properties);

        TicketReviewDecision decision = service.decide(new TicketTriageAnalysis(
                "llm-json-parser",
                "{}",
                null,
                AnalysisStatus.FAILED,
                TicketCategory.OTHER,
                TicketPriority.MEDIUM,
                "Analysis failed.",
                List.of(),
                List.of("Model output was not valid JSON.")
        ));

        assertThat(decision.reviewStatus()).isEqualTo(ReviewStatus.NEEDS_REVIEW);
        assertThat(decision.reviewReason()).isEqualTo("Analysis failed validation.");
    }

    @Test
    void decideRequiresReviewWhenConfidenceIsBelowConfiguredThreshold() {
        TicketReviewProperties properties = new TicketReviewProperties();
        properties.setConfidenceThreshold(0.85);

        DefaultTicketReviewDecisionService service = new DefaultTicketReviewDecisionService(properties);

        TicketReviewDecision decision = service.decide(new TicketTriageAnalysis(
                "llm-json-parser",
                "{}",
                0.80,
                AnalysisStatus.VALIDATED,
                TicketCategory.ACCOUNT_ACCESS,
                TicketPriority.HIGH,
                "Customer needs help accessing account.",
                List.of(),
                List.of("Model output parsed successfully.")
        ));

        assertThat(decision.reviewStatus()).isEqualTo(ReviewStatus.NEEDS_REVIEW);
        assertThat(decision.reviewReason()).isEqualTo("Model confidence is below review threshold.");
    }

    @Test
    void decideDoesNotRequireReviewWhenConfidenceMeetsConfiguredThreshold() {
        TicketReviewProperties properties = new TicketReviewProperties();
        properties.setConfidenceThreshold(0.85);

        DefaultTicketReviewDecisionService service = new DefaultTicketReviewDecisionService(properties);

        TicketReviewDecision decision = service.decide(new TicketTriageAnalysis(
                "llm-json-parser",
                "{}",
                0.90,
                AnalysisStatus.VALIDATED,
                TicketCategory.ACCOUNT_ACCESS,
                TicketPriority.HIGH,
                "Customer needs help accessing account.",
                List.of(),
                List.of("Model output parsed successfully.")
        ));

        assertThat(decision.reviewStatus()).isEqualTo(ReviewStatus.NOT_REQUIRED);
        assertThat(decision.reviewReason()).isNull();
    }

    @Test
    void decideDoesNotRequireReviewWhenConfidenceIsMissingAndAnalysisIsValidated() {
        TicketReviewProperties properties = new TicketReviewProperties();
        DefaultTicketReviewDecisionService service = new DefaultTicketReviewDecisionService(properties);

        TicketReviewDecision decision = service.decide(new TicketTriageAnalysis(
                "deterministic-stub",
                null,
                null,
                AnalysisStatus.VALIDATED,
                TicketCategory.ACCOUNT_ACCESS,
                TicketPriority.URGENT,
                "Customer needs help accessing account.",
                List.of(),
                List.of("Request passed API validation.")
        ));

        assertThat(decision.reviewStatus()).isEqualTo(ReviewStatus.NOT_REQUIRED);
        assertThat(decision.reviewReason()).isNull();
    }
}