package com.msvc.catalog.shared.responses;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private boolean success;
    private String message;
    private String traceId;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse() {
    }

    public ApiResponse(boolean success, String message, String traceId, T data, LocalDateTime timestamp) {
        this.success = success;
        this.message = message;
        this.traceId = traceId;
        this.data = data;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
