package com.aliniaz.tickettriage.ticket.service.impl;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import com.aliniaz.tickettriage.ticket.service.TicketAnalysisService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.containsAny;
import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.normalize;

@Service
public class TicketAnalysisServiceImpl implements TicketAnalysisService {

    @Override
    public TicketAnalysisResponse analyze(TicketAnalysisRequest request) {
        String normalizedText = normalize(request.subject() + " " + request.body());

        TicketCategory category = classify(normalizedText);
        TicketPriority priority = assignPriority(normalizedText, category);
        List<String> missingInformation = detectMissingInformation(request, category);

        return new TicketAnalysisResponse(
                1L,
                "deterministic-stub",
                AnalysisStatus.VALIDATED,
                category,
                priority,
                describeIntent(category),
                missingInformation,
                List.of("Request passed API validation.", "LLM integration has not been added yet.")
        );
    }

    private TicketCategory classify(String text) {
        if (containsAny(text, "invoice", "billing", "payment", "refund", "charged")) {
            return TicketCategory.BILLING;
        }

        if (containsAny(text, "login", "password", "account locked", "cannot access", "sign in")) {
            return TicketCategory.ACCOUNT_ACCESS;
        }

        if (containsAny(text, "error", "bug", "crash", "failed", "not working", "broken")) {
            return TicketCategory.TECHNICAL_ISSUE;
        }

        if (containsAny(text, "feature", "request", "enhancement", "can you add")) {
            return TicketCategory.FEATURE_REQUEST;
        }

        if (containsAny(text, "complaint", "angry", "unhappy", "frustrated", "terrible")) {
            return TicketCategory.COMPLAINT;
        }

        return TicketCategory.OTHER;
    }

    private TicketPriority assignPriority(String text, TicketCategory category) {
        if (containsAny(text, "urgent", "production down", "blocked", "cannot access")) {
            return TicketPriority.URGENT;
        }

        if (category == TicketCategory.COMPLAINT || category == TicketCategory.TECHNICAL_ISSUE) {
            return TicketPriority.HIGH;
        }

        if (category == TicketCategory.FEATURE_REQUEST) {
            return TicketPriority.LOW;
        }

        return TicketPriority.MEDIUM;
    }

    private List<String> detectMissingInformation(TicketAnalysisRequest request, TicketCategory category) {
        List<String> missing = new ArrayList<>();

        if (request.customerId() == null || request.customerId().isBlank()) {
            missing.add("customerId");
        }

        if (category == TicketCategory.TECHNICAL_ISSUE) {
            String body = normalize(request.body());

            if (!containsAny(body, "steps", "reproduce", "after clicking", "when i")) {
                missing.add("stepsToReproduce");
            }

            if (!containsAny(body, "error", "screenshot", "stack trace", "message")) {
                missing.add("errorMessageOrScreenshot");
            }
        }

        return missing;
    }

    private String describeIntent(TicketCategory category) {
        return switch (category) {
            case BILLING -> "Customer needs help with a billing or payment issue.";
            case TECHNICAL_ISSUE -> "Customer is reporting a technical issue.";
            case ACCOUNT_ACCESS -> "Customer needs help accessing their account.";
            case FEATURE_REQUEST -> "Customer is requesting a product improvement.";
            case COMPLAINT -> "Customer is expressing dissatisfaction and needs follow-up.";
            case OTHER -> "Customer request needs manual review.";
        };
    }
}
