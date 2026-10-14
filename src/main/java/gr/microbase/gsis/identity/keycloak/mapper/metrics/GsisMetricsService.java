package gr.microbase.gsis.identity.keycloak.mapper.metrics;

import io.micrometer.core.instrument.Metrics;

public class GsisMetricsService {

    private static final String UNKNOWN = "unknown";

    public GsisMetricsService() {
    }

    public void incrementApiAttempt(String mapper, String endpoint) {
        Metrics.counter("gsis_userid_api_attempts_total",
                "mapper", valueOrUnknown(mapper),
                "endpoint", valueOrUnknown(endpoint))
                .increment();
    }

    public void incrementApiFailure(String mapper, String endpoint, String failureType) {
        Metrics.counter("gsis_userid_api_failures_total",
                "mapper", valueOrUnknown(mapper),
                "endpoint", valueOrUnknown(endpoint),
                "failure_type", valueOrUnknown(failureType))
                .increment();
    }

    public void incrementAuthFailure(String mapper, String reason) {
        Metrics.counter("gsis_userid_auth_failures_total",
                "mapper", valueOrUnknown(mapper),
                "reason", valueOrUnknown(reason))
                .increment();
    }

    private static String valueOrUnknown(String value) {
        return value == null || value.isBlank() ? UNKNOWN : value;
    }
}
