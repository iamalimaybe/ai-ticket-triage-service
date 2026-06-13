package com.aliniaz.tickettriage.ticket.analysis.validator;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.validator.dto.TicketTriageAnalysisValidationResult;

public interface TicketTriageAnalysisValidator {

    TicketTriageAnalysisValidationResult validate(TicketTriageAnalysis analysis);
}