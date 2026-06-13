package com.aliniaz.tickettriage.ticket.api.error;

import com.aliniaz.tickettriage.ticket.api.error.dto.ApiErrorResponse;
import com.aliniaz.tickettriage.ticket.api.error.dto.ApiValidationError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ApiValidationError> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Request validation failed.",
                request.getRequestURI(),
                validationErrors
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Invalid request parameter value.",
                request.getRequestURI(),
                List.of(new ApiValidationError(
                        exception.getName(),
                        "Invalid value: " + exception.getValue()
                ))
        ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatusException(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        int statusCode = exception.getStatusCode().value();
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

        return ResponseEntity.status(exception.getStatusCode()).body(new ApiErrorResponse(
                Instant.now(),
                statusCode,
                httpStatus.getReasonPhrase(),
                exception.getReason(),
                request.getRequestURI(),
                List.of()
        ));
    }

    private ApiValidationError toValidationError(FieldError fieldError) {
        return new ApiValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage()
        );
    }
}