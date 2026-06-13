package com.aliniaz.tickettriage.ticket.api;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisReviewRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisDetailResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisListResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import com.aliniaz.tickettriage.ticket.service.TicketAnalysisService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketAnalysisController {
    private final TicketAnalysisService ticketAnalysisService;

    public TicketAnalysisController(TicketAnalysisService ticketAnalysisService) {
        this.ticketAnalysisService = ticketAnalysisService;
    }

    @PostMapping("/analyze")
    public TicketAnalysisResponse analyze(@Valid @RequestBody TicketAnalysisRequest request) {
        return ticketAnalysisService.analyze(request);
    }

    @GetMapping("/analyses/{analysisId}")
    public TicketAnalysisDetailResponse getAnalysis(@PathVariable Long analysisId) {
        return ticketAnalysisService.getAnalysis(analysisId);
    }

    @GetMapping("/analyses")
    public TicketAnalysisListResponse listAnalyses(
            @RequestParam(required = false) ReviewStatus reviewStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ticketAnalysisService.listAnalyses(reviewStatus, page, size);
    }

    @PatchMapping("/analyses/{analysisId}/review")
    public TicketAnalysisDetailResponse updateReviewStatus(
            @PathVariable Long analysisId,
            @Valid @RequestBody TicketAnalysisReviewRequest request
    ) {
        return ticketAnalysisService.updateReviewStatus(analysisId, request);
    }
}
