package com.aliniaz.tickettriage.ticket.analysis.impl;

import com.aliniaz.tickettriage.ticket.analysis.TicketTriageAnalyzer;
import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.llm.LlmClient;
import com.aliniaz.tickettriage.ticket.analysis.parser.TicketTriageOutputParser;
import com.aliniaz.tickettriage.ticket.analysis.prompt.TicketTriagePromptBuilder;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        prefix = "ticket.triage.analyzer",
        name = "mode",
        havingValue = "ollama"
)
public class OllamaTicketTriageAnalyzer implements TicketTriageAnalyzer {

    private final TicketTriagePromptBuilder promptBuilder;
    private final LlmClient llmClient;
    private final TicketTriageOutputParser outputParser;

    @Override
    public TicketTriageAnalysis analyze(TicketAnalysisRequest request) {
        try {
            String prompt = promptBuilder.buildPrompt(request);
            String rawOutput = llmClient.generate(prompt);

            return outputParser.parse(rawOutput);
        } catch (RuntimeException exception) {
            return new TicketTriageAnalysis(
                    "ollama-analyzer",
                    null,
                    AnalysisStatus.FAILED,
                    TicketCategory.OTHER,
                    TicketPriority.MEDIUM,
                    "LLM analysis failed and requires manual review.",
                    Collections.emptyList(),
                    List.of("LLM analysis failed: " + exception.getMessage())
            );
        }
    }
}