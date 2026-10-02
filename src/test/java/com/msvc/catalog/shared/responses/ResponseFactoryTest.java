package com.msvc.catalog.shared.responses;

import com.msvc.catalog.shared.tracing.TraceProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResponseFactoryTest {

    @Mock
    private Clock clock;

    @Mock
    private TraceProvider traceProvider;

    private ResponseFactory responseFactory;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // Instanciamos el ResponseFactory REAL con los mocks inyectados
        responseFactory = new ResponseFactory(clock, traceProvider);
    }

    @Test
    @DisplayName("Should create success response with data")
    void shouldCreateSuccessResponseWithData() {
        // Given
        when(traceProvider.getTraceId()).thenReturn("test-trace-id");
        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // When
        ApiResponse<String> response = responseFactory.success("Test message", "data");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getMessage()).isEqualTo("Test message");
        assertThat(response.getData()).isEqualTo("data");
        assertThat(response.getTraceId()).isEqualTo("test-trace-id");
    }

    @Test
    @DisplayName("Should create error response")
    void shouldCreateErrorResponse() {
        // Given
        when(traceProvider.getTraceId()).thenReturn("test-trace-id");
        when(clock.instant()).thenReturn(Instant.now());
        when(clock.getZone()).thenReturn(ZoneOffset.UTC);

        // When
        ApiErrorResponse response = responseFactory.error(
                HttpStatus.NOT_FOUND, "Not found"
        );

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getMessage()).isEqualTo("Not found");
    }
}