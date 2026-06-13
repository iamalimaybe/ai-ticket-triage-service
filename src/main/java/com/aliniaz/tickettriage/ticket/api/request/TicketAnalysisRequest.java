package com.aliniaz.tickettriage.ticket.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TicketAnalysisRequest(
        @NotBlank
        @Size(max = 120)
        String subject,

        @NotBlank
        @Size(max = 5000)
        String body,

        @Size(max = 80)
        String customerId
) {
}
