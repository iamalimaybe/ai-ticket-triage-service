package com.aliniaz.tickettriage.ticket.analysis.validator.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.validator.dto.TicketTriageAnalysisValidationResult;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTicketTriageAnalysisValidatorTest {

    private final DefaultTicketTriageAnalysisValidator validator = new DefaultTicketTriageAnalysisValidator();

    @Test
    void validateReturnsValidForCompleteAnalysis() {
        TicketTriageAnalysis analysis = new TicketTriageAnalysis(
                "deterministic-stub",
                AnalysisStatus.VALIDATED,
                TicketCategory.ACCOUNT_ACCESS,
                TicketPriority.URGENT,
                "Customer needs help accessing their account.",
                List.of(),
                List.of("Request passed API validation.")
        );

        TicketTriageAnalysisValidationResult result = validator.validate(analysis);

        assertThat(result.valid()).isTrue();
        assertThat(result.messages()).isEmpty();
    }

    @Test
    void validateReturnsInvalidForIncompleteAnalysis() {
        TicketTriageAnalysis analysis = new TicketTriageAnalysis(
                "",
                null,
                null,
                null,
                "",
                null,
                null
        );

        TicketTriageAnalysisValidationResult result = validator.validate(analysis);

        assertThat(result.valid()).isFalse();
        assertThat(result.messages()).contains(
                "analysisSource is required.",
                "status is required.",
                "category is required.",
                "priority is required.",
                "customerIntent is required.",
                "missingInformation list is required.",
                "validationMessages list is required."
        );
    }
}