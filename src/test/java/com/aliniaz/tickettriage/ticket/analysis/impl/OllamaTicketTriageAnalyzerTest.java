package com.aliniaz.tickettriage.ticket.analysis.impl;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.llm.LlmClient;
import com.aliniaz.tickettriage.ticket.analysis.parser.TicketTriageOutputParser;
import com.aliniaz.tickettriage.ticket.analysis.prompt.TicketTriagePromptBuilder;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OllamaTicketTriageAnalyzerTest {

    private final TicketTriagePromptBuilder promptBuilder = mock(TicketTriagePromptBuilder.class);
    private final LlmClient llmClient = mock(LlmClient.class);
    private final TicketTriageOutputParser outputParser = mock(TicketTriageOutputParser.class);

    private final OllamaTicketTriageAnalyzer analyzer = new OllamaTicketTriageAnalyzer(
            promptBuilder,
            llmClient,
            outputParser
    );

    @Test
    void analyzeBuildsPromptCallsLlmAndParsesOutput() {
        TicketAnalysisRequest request = new TicketAnalysisRequest(
                "Cannot login",
                "I cannot access my account.",
                "CUST-1001"
        );

        TicketTriageAnalysis parsedAnalysis = new TicketTriageAnalysis(
                "llm-json-parser",
                null,
                AnalysisStatus.VALIDATED,
                TicketCategory.ACCOUNT_ACCESS,
                TicketPriority.URGENT,
                "Customer needs help accessing their account.",
                List.of(),
                List.of("Model output parsed successfully.")
        );

        when(promptBuilder.buildPrompt(request)).thenReturn("prompt");
        when(llmClient.generate("prompt")).thenReturn("{\"category\":\"ACCOUNT_ACCESS\"}");
        when(outputParser.parse("{\"category\":\"ACCOUNT_ACCESS\"}")).thenReturn(parsedAnalysis);

        TicketTriageAnalysis result = analyzer.analyze(request);

        assertThat(result).isEqualTo(parsedAnalysis);

        verify(promptBuilder).buildPrompt(request);
        verify(llmClient).generate("prompt");
        verify(outputParser).parse("{\"category\":\"ACCOUNT_ACCESS\"}");
        verifyNoMoreInteractions(promptBuilder, llmClient, outputParser);
    }

    @Test
    void analyzeReturnsFailedAnalysisWhenLlmClientFails() {
        TicketAnalysisRequest request = new TicketAnalysisRequest(
                "Cannot login",
                "I cannot access my account.",
                "CUST-1001"
        );

        when(promptBuilder.buildPrompt(request)).thenReturn("prompt");
        when(llmClient.generate("prompt")).thenThrow(new IllegalStateException("Ollama returned an empty response."));

        TicketTriageAnalysis result = analyzer.analyze(request);

        assertThat(result.analysisSource()).isEqualTo("ollama-analyzer");
        assertThat(result.status()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(result.category()).isEqualTo(TicketCategory.OTHER);
        assertThat(result.priority()).isEqualTo(TicketPriority.MEDIUM);
        assertThat(result.customerIntent()).isEqualTo("LLM analysis failed and requires manual review.");
        assertThat(result.validationMessages()).contains("LLM analysis failed: Ollama returned an empty response.");

        verify(promptBuilder).buildPrompt(request);
        verify(llmClient).generate("prompt");
        verifyNoInteractions(outputParser);
    }
}