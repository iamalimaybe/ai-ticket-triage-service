package com.aliniaz.tickettriage.ticket.service;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisDetailResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisListResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;

public interface TicketAnalysisService {
    TicketAnalysisResponse analyze(TicketAnalysisRequest request);
    TicketAnalysisDetailResponse getAnalysis(Long analysisId);

    TicketAnalysisListResponse listAnalyses(ReviewStatus reviewStatus, int page, int size);
}
