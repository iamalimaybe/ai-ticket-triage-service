package com.aliniaz.tickettriage.ticket.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket_analysis")
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

    @ElementCollection
    @CollectionTable(
            name = "ticket_analysis_missing_information",
            joinColumns = @JoinColumn(name = "ticket_analysis_id")
    )
    @Column(name = "field_name", nullable = false, length = 120)
    private List<String> missingInformation = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "ticket_analysis_validation_messages",
            joinColumns = @JoinColumn(name = "ticket_analysis_id")
    )
    @Column(name = "message", nullable = false, length = 500)
    private List<String> validationMessages = new ArrayList<>();

    protected TicketAnalysis() {
    }

    public TicketAnalysis(
            String subject,
            String body,
            String customerId,
            String analysisSource,
            AnalysisStatus status,
            TicketCategory category,
            TicketPriority priority,
            String customerIntent,
            List<String> missingInformation,
            List<String> validationMessages
    ) {
        this.subject = subject;
        this.body = body;
        this.customerId = customerId;
        this.analysisSource = analysisSource;
        this.status = status;
        this.category = category;
        this.priority = priority;
        this.customerIntent = customerIntent;
        this.missingInformation = new ArrayList<>(missingInformation);
        this.validationMessages = new ArrayList<>(validationMessages);
    }

    public Long getId() {
        return id;
    }

    public String getAnalysisSource() {
        return analysisSource;
    }

    public AnalysisStatus getStatus() {
        return status;
    }

    public TicketCategory getCategory() {
        return category;
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public String getCustomerIntent() {
        return customerIntent;
    }

    public List<String> getMissingInformation() {
        return missingInformation;
    }

    public List<String> getValidationMessages() {
        return validationMessages;
    }
}