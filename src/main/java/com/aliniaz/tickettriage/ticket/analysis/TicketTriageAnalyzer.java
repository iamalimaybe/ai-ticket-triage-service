package com.aliniaz.tickettriage.ticket.analysis;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;

public interface TicketTriageAnalyzer {
    TicketTriageAnalysis analyze(TicketAnalysisRequest request);
}
