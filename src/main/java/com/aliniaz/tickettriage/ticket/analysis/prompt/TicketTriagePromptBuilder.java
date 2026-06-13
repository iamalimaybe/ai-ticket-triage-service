package com.aliniaz.tickettriage.ticket.analysis.prompt;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;

public interface TicketTriagePromptBuilder {

    String buildPrompt(TicketAnalysisRequest request);
}