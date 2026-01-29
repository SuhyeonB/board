package com.example.board.global.common.response;


import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String code;
    private final String message;
    private final List<FieldError> errors;

    public ErrorResponse(LocalDateTime timestamp, int status, String code, String message, List<FieldError> errors) {
        this.timestamp = timestamp;
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = errors;
    }

    public static ErrorResponse of (int status, String code, String message) {
        return new ErrorResponse(LocalDateTime.now(), status, code, message, List.of());
    }

    public static ErrorResponse of (int status, String code, String message, List<FieldError> errors) {
        return new ErrorResponse(LocalDateTime.now(), status, code, message, errors == null ?  List.of() : errors);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getErrors() {
        return errors;
    }

    public static class FieldError {
        private final String field;
        private final Object rejectedValue;
        private final String reason;

        public FieldError(String field, Object rejectedValue, String reason) {
            this.field = field;
            this.rejectedValue = rejectedValue;
            this.reason = reason;
        }

        public static FieldError of(String field, Object rejectedValue, String reason) {
            return new FieldError(field, rejectedValue, reason);
        }

        public String getField() {
            return field;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }

        public String getReason() {
            return reason;
        }
    }
}
