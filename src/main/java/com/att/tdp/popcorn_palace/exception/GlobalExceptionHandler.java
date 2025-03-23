package com.att.tdp.popcorn_palace.exception;

import com.att.tdp.popcorn_palace.validation.ShowtimeValidator;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        String errStr = "Validation failed. " + ex.getBindingResult().getAllErrors().stream().map(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            return fieldName + ": " + errorMessage;
        }).collect(Collectors.joining(", "));

        errors.put("error", errStr);
        logger.warn("Validation failed: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        logger.warn("Data integrity violation: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        String message = ex.getMostSpecificCause().getMessage();
        String err = "Database constraint violation occurred";

        if (message.contains("unique constraint") || message.contains("Duplicate entry")) {
            if (message.contains("duplicate key value violates unique constraint \"movies_title_key\"")) {
                err = "A movie with the same title already exists";
            } else if (message.contains("duplicate key value violates unique constraint \"theaters_name_key\"")) {
                err = "A theater with the same name already exists";
            } else {
                err = "A record with the same unique identifier already exists";
            }
        }

        errors.put("error", err);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(NoResourceFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        logger.warn("Media type not supported: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Media type not supported");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String message = violation.getMessage();

            // ShowtimeValidator
            if (violation.getConstraintDescriptor().getAnnotation() instanceof ShowtimeValidator) {
                message = "Showtime validation failed: " + message;
            }

            errors.put("error", message);
        });

        logger.warn("Constraint violation: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        logger.warn("Type mismatch: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        String paramName = ex.getName();
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String providedValue = ex.getValue() != null ? ex.getValue().toString() : "null";

        String errorMessage = String.format(
                "Parameter '%s' must be a valid %s, but received: '%s'",
                paramName, requiredType, providedValue);

        errors.put("error", errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingRequestBody(HttpMessageNotReadableException ex) {
        logger.warn("Request body error: {}", ex.getMessage());

        Map<String, String> errors = new HashMap<>();
        String errorMessage = "Required request body is missing";
        if (ex.getMessage() != null && !ex.getMessage().contains("popcorn")) {
            errorMessage += ": " + ex.getMessage();
        }

        errors.put("error", errorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTransactionSystemException(TransactionSystemException ex) {
        logger.error("Transaction error: {}", ex.getMessage());

        // Check if the root cause is a ConstraintViolationException
        Throwable rootCause = ex.getRootCause();
        if (rootCause instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) rootCause);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("error", "An error occurred while processing your request");
        response.put("message", ex.getMostSpecificCause().getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ShowtimeValidationException.class)
    public ResponseEntity<Map<String, Object>> handleShowtimeValidationException(ShowtimeValidationException ex) {
        logger.warn("Showtime validation error: {}", ex.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("error", "Validation failed");
        response.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //////

    @ExceptionHandler(Exception.class) // Generic fallback
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex) {
        logger.error("Unexpected error: {}", ex.getMessage());

        Map<String, String> response = new HashMap<>();

        String err = "An unexpected error occurred";
        if (ex.getMessage().contains("Request method")) {
            err = "Request method not supported";
        } else {
            logger.error("Stack trace: ", ex);
        }

        response.put("error", err);
        return ResponseEntity.internalServerError().body(response);
    }
}

