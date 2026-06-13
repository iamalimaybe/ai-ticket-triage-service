package com.aliniaz.tickettriage.ticket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket_analysis")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class TicketAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject", nullable = false, length = 120)
    private String subject;

    @Column(name = "body", nullable = false, length = 5000)
    private String body;

    @Column(name = "customer_id", length = 80)
    private String customerId;

    @Column(name = "analysis_source", nullable = false, length = 80)
    private String analysisSource;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private AnalysisStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 40)
    private TicketCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 40)
    private TicketPriority priority;

    @Column(name = "customer_intent", nullable = false, length = 500)
    private String customerIntent;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "ticket_analysis_missing_information",
            joinColumns = @JoinColumn(name = "ticket_analysis_id")
    )
    @Column(name = "field_name", nullable = false, length = 120)
    private List<String> missingInformation = new ArrayList<>();

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "ticket_analysis_validation_messages",
            joinColumns = @JoinColumn(name = "ticket_analysis_id")
    )
    @Column(name = "message", nullable = false, length = 500)
    private List<String> validationMessages = new ArrayList<>();

    @Column(name = "raw_model_output", columnDefinition = "TEXT")
    private String rawModelOutput;

    @Column(name = "model_confidence")
    private Double modelConfidence;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private Instant updatedAt;
}