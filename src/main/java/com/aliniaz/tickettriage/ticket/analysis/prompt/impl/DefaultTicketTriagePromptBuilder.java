package com.aliniaz.tickettriage.ticket.analysis.prompt.impl;

import com.aliniaz.tickettriage.ticket.analysis.prompt.TicketTriagePromptBuilder;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import org.springframework.stereotype.Component;

@Component
public class DefaultTicketTriagePromptBuilder implements TicketTriagePromptBuilder {

    @Override
    public String buildPrompt(TicketAnalysisRequest request) {
        return """
                You are analyzing a customer support ticket.

                Return only valid JSON.
                Do not include markdown.
                Do not include explanations outside JSON.
                Do not invent facts that are not present in the ticket.

                Allowed categories:
                BILLING
                TECHNICAL_ISSUE
                ACCOUNT_ACCESS
                FEATURE_REQUEST
                COMPLAINT
                OTHER

                Allowed priorities:
                LOW
                MEDIUM
                HIGH
                URGENT

                Required JSON shape:
                {
                  "category": "ACCOUNT_ACCESS",
                  "priority": "HIGH",
                  "customerIntent": "Short sentence describing what the customer needs.",
                  "missingInformation": ["fieldName"],
                  "confidence": 0.0
                }

                Rules:
                1. category must be one of the allowed categories.
                2. priority must be one of the allowed priorities.
                3. customerIntent must be a short sentence.
                4. missingInformation must be an array, even if empty.
                5. confidence must be a number between 0.0 and 1.0.
                6. If the ticket is unclear, use category OTHER and lower confidence.
                7. If technical details are missing for a technical issue, include missing fields such as stepsToReproduce or errorMessageOrScreenshot.
                8. If customerId is missing, include customerId in missingInformation.

                Ticket:
                Subject: %s
                Body: %s
                Customer ID: %s
                """.formatted(
                request.subject(),
                request.body(),
                request.customerId() == null || request.customerId().isBlank()
                        ? "[missing]"
                        : request.customerId()
        );
    }
}