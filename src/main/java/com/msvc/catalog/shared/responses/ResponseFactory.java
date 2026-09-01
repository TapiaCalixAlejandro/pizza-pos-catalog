package com.msvc.catalog.shared.responses;

import com.msvc.catalog.shared.tracing.TraceProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ResponseFactory {

    private final TraceProvider traceProvider;

    public ResponseFactory(TraceProvider traceProvider) {
        this.traceProvider = traceProvider;
    }

    public String getTraceId() {
        return traceProvider.getTraceId();
    }

    public <T> ApiResponse<T> success(
            String message,
            T data
    ) {

        return new ApiResponse<>(
                true,
                message,
                getTraceId(),
                data,
                LocalDateTime.now()
        );
    }

    public ApiErrorResponse error(
            HttpStatus status,
            String message,
            List<String> details
    ) {

        return new ApiErrorResponse(
                false,
                status.value(),
                status.getReasonPhrase(),
                message,
                details,
                getTraceId(),
                LocalDateTime.now()
        );
    }

    public ApiErrorResponse error(
            HttpStatus status,
            String message
    ) {

        return  error(status, message, List.of());
    }

}
