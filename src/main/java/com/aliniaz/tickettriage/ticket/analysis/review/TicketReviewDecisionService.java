package com.aliniaz.tickettriage.ticket.analysis.review;

import com.aliniaz.tickettriage.ticket.analysis.dto.TicketTriageAnalysis;
import com.aliniaz.tickettriage.ticket.analysis.review.dto.TicketReviewDecision;

public interface TicketReviewDecisionService {

    TicketReviewDecision decide(TicketTriageAnalysis analysis);
}