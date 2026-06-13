package com.aliniaz.tickettriage.ticket.analysis.validator.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.validator.TicketTriageAnalysisValidator;
import com.aliniaz.tickettriage.ticket.analysis.validator.dto.TicketTriageAnalysisValidationResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.isBlank;

@Component
public class DefaultTicketTriageAnalysisValidator implements TicketTriageAnalysisValidator {

    @Override
    public TicketTriageAnalysisValidationResult validate(TicketTriageAnalysis analysis) {
        List<String> messages = new ArrayList<>();

        if (analysis == null) {
            return new TicketTriageAnalysisValidationResult(
                    false,
                    List.of("Analyzer output is required.")
            );
        }

        if (isBlank(analysis.analysisSource())) {
            messages.add("analysisSource is required.");
        }

        if (analysis.status() == null) {
            messages.add("status is required.");
        }

        if (analysis.category() == null) {
            messages.add("category is required.");
        }

        if (analysis.priority() == null) {
            messages.add("priority is required.");
        }

        if (isBlank(analysis.customerIntent())) {
            messages.add("customerIntent is required.");
        }

        if (analysis.modelConfidence() != null
                && (analysis.modelConfidence() < 0.0 || analysis.modelConfidence() > 1.0)) {
            messages.add("modelConfidence must be between 0.0 and 1.0.");
        }

        if (analysis.missingInformation() == null) {
            messages.add("missingInformation list is required.");
        }

        if (analysis.validationMessages() == null) {
            messages.add("validationMessages list is required.");
        }

        return new TicketTriageAnalysisValidationResult(
                messages.isEmpty(),
                messages
        );
    }
}