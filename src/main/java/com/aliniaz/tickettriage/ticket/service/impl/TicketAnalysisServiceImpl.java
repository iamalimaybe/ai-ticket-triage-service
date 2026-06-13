package com.aliniaz.tickettriage.ticket.service.impl;

import com.aliniaz.tickettriage.ticket.analysis.TicketTriageAnalyzer;
import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisDetailResponse;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketAnalysis;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import com.aliniaz.tickettriage.ticket.repository.TicketAnalysisRepository;
import com.aliniaz.tickettriage.ticket.service.TicketAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.containsAny;
import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.normalize;

@Service
public class TicketAnalysisServiceImpl implements TicketAnalysisService {

    private final TicketAnalysisRepository ticketAnalysisRepository;
    private final TicketTriageAnalyzer ticketTriageAnalyzer;

    public TicketAnalysisServiceImpl(
            TicketAnalysisRepository ticketAnalysisRepository,
            TicketTriageAnalyzer ticketTriageAnalyzer
    ) {
        this.ticketAnalysisRepository = ticketAnalysisRepository;
        this.ticketTriageAnalyzer = ticketTriageAnalyzer;
    }

    @Override
    @Transactional
    public TicketAnalysisResponse analyze(TicketAnalysisRequest request) {
        TicketTriageAnalysis analysis = ticketTriageAnalyzer.analyze(request);

        TicketAnalysis ticketAnalysis = TicketAnalysis.builder()
                .subject(request.subject())
                .body(request.body())
                .customerId(request.customerId())
                .analysisSource(analysis.analysisSource())
                .status(analysis.status())
                .category(analysis.category())
                .priority(analysis.priority())
                .customerIntent(analysis.customerIntent())
                .missingInformation(analysis.missingInformation())
                .validationMessages(analysis.validationMessages())
                .build();

        TicketAnalysis savedTicketAnalysis = ticketAnalysisRepository.save(ticketAnalysis);

        return toResponse(savedTicketAnalysis);
    }

    private TicketAnalysisResponse toResponse(TicketAnalysis ticketAnalysis) {
        return new TicketAnalysisResponse(
                ticketAnalysis.getId(),
                ticketAnalysis.getAnalysisSource(),
                ticketAnalysis.getStatus(),
                ticketAnalysis.getCategory(),
                ticketAnalysis.getPriority(),
                ticketAnalysis.getCustomerIntent(),
                List.copyOf(ticketAnalysis.getMissingInformation()),
                List.copyOf(ticketAnalysis.getValidationMessages())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TicketAnalysisDetailResponse getAnalysis(Long analysisId) {
        TicketAnalysis ticketAnalysis = ticketAnalysisRepository.findById(analysisId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Ticket analysis not found"
                ));

        return toDetailResponse(ticketAnalysis);
    }

    private TicketAnalysisDetailResponse toDetailResponse(TicketAnalysis ticketAnalysis) {
        return new TicketAnalysisDetailResponse(
                ticketAnalysis.getId(),
                ticketAnalysis.getSubject(),
                ticketAnalysis.getBody(),
                ticketAnalysis.getCustomerId(),
                ticketAnalysis.getAnalysisSource(),
                ticketAnalysis.getStatus(),
                ticketAnalysis.getCategory(),
                ticketAnalysis.getPriority(),
                ticketAnalysis.getCustomerIntent(),
                List.copyOf(ticketAnalysis.getMissingInformation()),
                List.copyOf(ticketAnalysis.getValidationMessages())
        );
    }
}
