package com.msvc.catalog.shared.tracing;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Component
public class DefaultTraceProvider implements TraceProvider {

    private static final String TRACE_ID_ATTRIBUTE = "TRACE_ID";

    @Override
    public String getTraceId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return UUID.randomUUID().toString();
        }

        HttpServletRequest request = attributes.getRequest();

        Object traceId = request.getAttribute(TRACE_ID_ATTRIBUTE);

        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
            request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);
        }

        return traceId.toString();
    }

}
