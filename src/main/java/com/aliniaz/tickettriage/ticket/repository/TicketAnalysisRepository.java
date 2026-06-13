package com.aliniaz.tickettriage.ticket.repository;

import com.aliniaz.tickettriage.ticket.domain.TicketAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAnalysisRepository extends JpaRepository<TicketAnalysis, Long> {
}