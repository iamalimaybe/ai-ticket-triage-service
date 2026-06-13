package com.aliniaz.tickettriage.ticket.repository;

import com.aliniaz.tickettriage.ticket.domain.ReviewStatus;
import com.aliniaz.tickettriage.ticket.domain.TicketAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketAnalysisRepository extends JpaRepository<TicketAnalysis, Long> {
    Page<TicketAnalysis> findByReviewStatus(ReviewStatus reviewStatus, Pageable pageable);
}