package com.aliniaz.tickettriage.ticket.api;

import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisDetailResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
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
}
