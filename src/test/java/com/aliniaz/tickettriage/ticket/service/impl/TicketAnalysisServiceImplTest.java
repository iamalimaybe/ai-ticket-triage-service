package com.aliniaz.tickettriage.ticket.service.impl;

import com.aliniaz.tickettriage.ticket.analysis.TicketTriageAnalyzer;
import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.validator.impl.DefaultTicketTriageAnalysisValidator;
import com.aliniaz.tickettriage.ticket.api.request.TicketAnalysisRequest;
import com.aliniaz.tickettriage.ticket.api.response.TicketAnalysisResponse;
import com.aliniaz.tickettriage.ticket.domain.AnalysisStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketAnalysis;
import com.aliniaz.tickettriage.ticket.domain.TicketCategory;
import com.aliniaz.tickettriage.ticket.domain.TicketPriority;
import com.aliniaz.tickettriage.ticket.repository.TicketAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TicketAnalysisServiceImplTest {

    private final TicketAnalysisRepository ticketAnalysisRepository = mock(TicketAnalysisRepository.class);
    private final TicketTriageAnalyzer ticketTriageAnalyzer = mock(TicketTriageAnalyzer.class);
    private final DefaultTicketTriageAnalysisValidator validator = new DefaultTicketTriageAnalysisValidator();

    private final TicketAnalysisServiceImpl service = new TicketAnalysisServiceImpl(
            ticketAnalysisRepository,
            ticketTriageAnalyzer,
            validator
    );

    @Test
    void analyzePersistsFailedResultWhenAnalyzerOutputIsInvalid() {
        TicketAnalysisRequest request = new TicketAnalysisRequest(
                "Application error",
                "The application is not working.",
                "CUST-1001"
        );

        TicketTriageAnalysis invalidAnalysis = new TicketTriageAnalysis(
                "",
                null,
                null,
                null,
                null,
                "",
                null,
                null
        );

        when(ticketTriageAnalyzer.analyze(request)).thenReturn(invalidAnalysis);

        ArgumentCaptor<TicketAnalysis> captor = ArgumentCaptor.forClass(TicketAnalysis.class);
        when(ticketAnalysisRepository.save(captor.capture()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TicketAnalysisResponse response = service.analyze(request);

        TicketAnalysis savedEntity = captor.getValue();

        assertThat(savedEntity.getAnalysisSource()).isEqualTo("unknown-analyzer");
        assertThat(savedEntity.getStatus()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(savedEntity.getCategory()).isEqualTo(TicketCategory.OTHER);
        assertThat(savedEntity.getPriority()).isEqualTo(TicketPriority.MEDIUM);
        assertThat(savedEntity.getCustomerIntent()).isEqualTo("Analysis failed validation and requires manual review.");
        assertThat(savedEntity.getMissingInformation()).isEmpty();
        assertThat(savedEntity.getValidationMessages()).contains(
                "analysisSource is required.",
                "status is required.",
                "category is required.",
                "priority is required.",
                "customerIntent is required.",
                "missingInformation list is required.",
                "validationMessages list is required."
        );

        assertThat(response.status()).isEqualTo(AnalysisStatus.FAILED);
        assertThat(response.category()).isEqualTo(TicketCategory.OTHER);
        assertThat(response.priority()).isEqualTo(TicketPriority.MEDIUM);

        verify(ticketTriageAnalyzer).analyze(request);
        verify(ticketAnalysisRepository).save(any(TicketAnalysis.class));
        verifyNoMoreInteractions(ticketTriageAnalyzer, ticketAnalysisRepository);
    }
}