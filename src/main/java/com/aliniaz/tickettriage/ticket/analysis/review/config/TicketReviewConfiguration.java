package com.aliniaz.tickettriage.ticket.analysis.review.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TicketReviewProperties.class)
public class TicketReviewConfiguration {
}