package com.aliniaz.tickettriage.ticket.api.response;

import java.util.List;

public record TicketAnalysisListResponse(
        List<TicketAnalysisSummaryResponse> items,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}