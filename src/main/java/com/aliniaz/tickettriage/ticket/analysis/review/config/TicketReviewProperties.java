package com.aliniaz.tickettriage.ticket.analysis.review.config;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "ticket.triage.review")
public class TicketReviewProperties {

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double confidenceThreshold = 0.70;
}