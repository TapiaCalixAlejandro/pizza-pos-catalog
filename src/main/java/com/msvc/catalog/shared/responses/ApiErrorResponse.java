package com.msvc.catalog.shared.responses;

import java.time.LocalDateTime;
import java.util.List;

public class ApiErrorResponse {

    private boolean success;
    private int status;
    private String error;
    private String message;
    private List<String> details;
    private String traceId;
    private LocalDateTime timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(
            boolean success,
            int status,
            String error,
            String message,
            List<String> details,
            String traceId,
            LocalDateTime timestamp
    ) {
        this.success = success;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
        this.traceId = traceId;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
