package com.aliniaz.tickettriage.ticket.analysis.parser;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;

public interface TicketTriageOutputParser {

    TicketTriageAnalysis parse(String rawOutput);
}