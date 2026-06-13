package com.aliniaz.tickettriage.ticket.analysis.parser.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTicketTriageOutputParserTest {

    private final DefaultTicketTriageOutputParser parser = new DefaultTicketTriageOutputParser(new ObjectMapper());

    @Test
    void parseReturnsValidatedAnalysisForValidModelJson() {
        String rawOutput = """
                {
                  "category": "ACCOUNT_ACCESS",
                  "priority": "URGENT",
                  "customerIntent": "Customer needs help accessing their account.",
                  "missingInformation": [],
                  "confidence": 0.91
                }
                """;

        TicketTriageAnalysis analysis = parser.parse(rawOutput);

        assertThat(analysis.analysisSource()).isEqualTo("llm-json-parser");
        assertThat(analysis.status()).isEqualTo(AnalysisStatus.VALIDATED);
        assertThat(analysis.rawModelOutput()).isEqualTo(rawOutput);
        assertThat(analysis.category()).isEqualTo(TicketCategory.ACCOUNT_ACCESS);
        assertThat(analysis.priority()).isEqualTo(TicketPriority.URGENT);
        assertThat(analysis.customerIntent()).isEqualTo("Customer needs help accessing their account.");
        assertThat(analysis.missingInformation()).isEmpty();
        assertThat(analysis.validationMessages()).contains("Model output parsed successfully.");
        assertThat(analysis.modelConfidence()).isEqualTo(0.91);
    }

    @Test
    void parseReturnsFailedAnalysisForInvalidJson() {
        TicketTriageAnalysis analysis = parser.parse("not-json");

        assertThat(analysis.status()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(analysis.category()).isEqualTo(TicketCategory.OTHER);
        assertThat(analysis.priority()).isEqualTo(TicketPriority.MEDIUM);
        assertThat(analysis.validationMessages()).contains("Model output was not valid JSON.");
    }

    @Test
    void parseReturnsFailedAnalysisForUnsupportedCategory() {
        String rawOutput = """
                {
                  "category": "SHIPPING",
                  "priority": "HIGH",
                  "customerIntent": "Customer wants delivery help.",
                  "missingInformation": [],
                  "confidence": 0.7
                }
                """;

        TicketTriageAnalysis analysis = parser.parse(rawOutput);

        assertThat(analysis.status()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(analysis.category()).isEqualTo(TicketCategory.OTHER);
        assertThat(analysis.validationMessages()).contains("Model output contained unsupported enum values.");
    }
}