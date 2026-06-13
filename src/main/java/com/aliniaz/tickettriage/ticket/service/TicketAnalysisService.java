package com.aliniaz.tickettriage.ticket.service;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;

public interface TicketAnalysisService {
    TicketAnalysisResponse analyze(TicketAnalysisRequest request);
}
