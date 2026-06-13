package com.aliniaz.tickettriage.ticket.api.error.dto;

public record ApiValidationError(
        String field,
        String message
) {
}