package com.aliniaz.tickettriage.ticket.analysis.parser.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.parser.TicketTriageOutputParser;
import com.aliniaz.tickettriage.ticket.analysis.parser.dto.LlmTicketTriageOutput;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.isBlank;

@Component
public class DefaultTicketTriageOutputParser implements TicketTriageOutputParser {

    private static final String ANALYSIS_SOURCE = "llm-json-parser";

    private final ObjectMapper objectMapper;

    public DefaultTicketTriageOutputParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public TicketTriageAnalysis parse(String rawOutput) {
        if (isBlank(rawOutput)) {
            return failedAnalysis("Model output is blank.");
        }

        try {
            LlmTicketTriageOutput output = objectMapper.readValue(rawOutput, LlmTicketTriageOutput.class);

            return new TicketTriageAnalysis(
                    ANALYSIS_SOURCE,
                    AnalysisStatus.VALIDATED,
                    parseCategory(output.category()),
                    parsePriority(output.priority()),
                    output.customerIntent(),
                    output.missingInformation() == null
                            ? Collections.emptyList()
                            : List.copyOf(output.missingInformation()),
                    List.of("Model output parsed successfully.")
            );
        } catch (JsonProcessingException exception) {
            return failedAnalysis("Model output was not valid JSON.");
        } catch (IllegalArgumentException exception) {
            return failedAnalysis("Model output contained unsupported enum values.");
        }
    }

    private TicketCategory parseCategory(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("category is required");
        }

        return TicketCategory.valueOf(value.trim());
    }

    private TicketPriority parsePriority(String value) {
        if (isBlank(value)) {
            throw new IllegalArgumentException("priority is required");
        }

        return TicketPriority.valueOf(value.trim());
    }

    private TicketTriageAnalysis failedAnalysis(String message) {
        return new TicketTriageAnalysis(
                ANALYSIS_SOURCE,
                AnalysisStatus.FAILED,
                TicketCategory.OTHER,
                TicketPriority.MEDIUM,
                "Model output could not be converted into a valid triage analysis.",
                Collections.emptyList(),
                List.of(message)
        );
    }
}