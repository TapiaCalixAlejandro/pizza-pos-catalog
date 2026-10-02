package com.msvc.catalog.shared.tracing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DefaultTraceProviderTest {

    private final DefaultTraceProvider traceProvider = new DefaultTraceProvider();

    @Test
    @DisplayName("Should generate a trace id")
    void shouldGenerateTraceId() {
        String traceId = traceProvider.getTraceId();

        assertThat(traceId).isNotNull().isNotEmpty();
    }
}