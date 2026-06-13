package com.aliniaz.tickettriage.ticket.analysis.prompt.impl;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTicketTriagePromptBuilderTest {

    private final DefaultTicketTriagePromptBuilder promptBuilder = new DefaultTicketTriagePromptBuilder();

    @Test
    void buildPromptIncludesTicketContentAndOutputContract() {
        TicketAnalysisRequest request = new TicketAnalysisRequest(
                "Cannot login",
                "I cannot access my account after password reset.",
                "CUST-1001"
        );

        String prompt = promptBuilder.buildPrompt(request);

        assertThat(prompt).contains("Return only valid JSON.");
        assertThat(prompt).contains("Allowed categories:");
        assertThat(prompt).contains("ACCOUNT_ACCESS");
        assertThat(prompt).contains("Allowed priorities:");
        assertThat(prompt).contains("URGENT");
        assertThat(prompt).contains("\"category\"");
        assertThat(prompt).contains("\"priority\"");
        assertThat(prompt).contains("\"customerIntent\"");
        assertThat(prompt).contains("\"missingInformation\"");
        assertThat(prompt).contains("\"confidence\"");
        assertThat(prompt).contains("Subject: Cannot login");
        assertThat(prompt).contains("Body: I cannot access my account after password reset.");
        assertThat(prompt).contains("Customer ID: CUST-1001");
    }

    @Test
    void buildPromptMarksMissingCustomerId() {
        TicketAnalysisRequest request = new TicketAnalysisRequest(
                "Application error",
                "The application is not working.",
                ""
        );

        String prompt = promptBuilder.buildPrompt(request);

        assertThat(prompt).contains("Customer ID: [missing]");
        assertThat(prompt).contains("If customerId is missing, include customerId in missingInformation.");
    }
}