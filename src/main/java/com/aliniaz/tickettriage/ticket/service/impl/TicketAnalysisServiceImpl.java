package com.aliniaz.tickettriage.ticket.service.impl;

import com.aliniaz.tickettriage.ticket.analysis.TicketTriageAnalyzer;
import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.validator.TicketTriageAnalysisValidator;
import com.aliniaz.tickettriage.ticket.analysis.validator.dto.TicketTriageAnalysisValidationResult;
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

import java.util.Collections;
import java.util.List;

import static com.aliniaz.tickettriage.ticket.utilities.StringUtil.isBlank;

@Service
public class TicketAnalysisServiceImpl implements TicketAnalysisService {

    private final TicketAnalysisRepository ticketAnalysisRepository;
    private final TicketTriageAnalyzer ticketTriageAnalyzer;
    private final TicketTriageAnalysisValidator ticketTriageAnalysisValidator;

    public TicketAnalysisServiceImpl(
            TicketAnalysisRepository ticketAnalysisRepository,
            TicketTriageAnalyzer ticketTriageAnalyzer,
            TicketTriageAnalysisValidator ticketTriageAnalysisValidator
    ) {
        this.ticketAnalysisRepository = ticketAnalysisRepository;
        this.ticketTriageAnalyzer = ticketTriageAnalyzer;
        this.ticketTriageAnalysisValidator = ticketTriageAnalysisValidator;
    }

    @Override
    @Transactional
    public TicketAnalysisResponse analyze(TicketAnalysisRequest request) {
        TicketTriageAnalysis analysis = ticketTriageAnalyzer.analyze(request);
        TicketTriageAnalysisValidationResult validationResult = ticketTriageAnalysisValidator.validate(analysis);
        TicketTriageAnalysis persistableAnalysis = toPersistableAnalysis(analysis, validationResult);

        TicketAnalysis ticketAnalysis = TicketAnalysis.builder()
                .subject(request.subject())
                .body(request.body())
                .customerId(request.customerId())
                .analysisSource(persistableAnalysis.analysisSource())
                .rawModelOutput(persistableAnalysis.rawModelOutput())
                .status(persistableAnalysis.status())
                .category(persistableAnalysis.category())
                .priority(persistableAnalysis.priority())
                .customerIntent(persistableAnalysis.customerIntent())
                .missingInformation(persistableAnalysis.missingInformation())
                .validationMessages(persistableAnalysis.validationMessages())
                .build();

        TicketAnalysis savedTicketAnalysis = ticketAnalysisRepository.save(ticketAnalysis);

        return toResponse(savedTicketAnalysis);
    }

    private TicketTriageAnalysis toPersistableAnalysis(
            TicketTriageAnalysis analysis,
            TicketTriageAnalysisValidationResult validationResult
    ) {
        if (validationResult.valid()) {
            return analysis;
        }

        return new TicketTriageAnalysis(
                analysis == null || isBlank(analysis.analysisSource()) ? "unknown-analyzer" : analysis.analysisSource(),
                analysis == null ? null : analysis.rawModelOutput(),
                AnalysisStatus.FAILED,
                analysis == null || analysis.category() == null ? TicketCategory.OTHER : analysis.category(),
                analysis == null || analysis.priority() == null ? TicketPriority.MEDIUM : analysis.priority(),
                analysis == null || isBlank(analysis.customerIntent())
                        ? "Analysis failed validation and requires manual review."
                        : analysis.customerIntent(),
                analysis == null || analysis.missingInformation() == null
                        ? Collections.emptyList()
                        : List.copyOf(analysis.missingInformation()),
                List.copyOf(validationResult.messages())
        );
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
                ticketAnalysis.getRawModelOutput(),
                ticketAnalysis.getStatus(),
                ticketAnalysis.getCategory(),
                ticketAnalysis.getPriority(),
                ticketAnalysis.getCustomerIntent(),
                List.copyOf(ticketAnalysis.getMissingInformation()),
                List.copyOf(ticketAnalysis.getValidationMessages()),
                ticketAnalysis.getCreatedAt(),
                ticketAnalysis.getUpdatedAt()
        );
    }
}
