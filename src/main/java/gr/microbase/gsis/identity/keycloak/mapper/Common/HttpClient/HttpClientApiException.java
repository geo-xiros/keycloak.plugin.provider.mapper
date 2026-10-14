package gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient;

import java.io.IOException;

public class HttpClientApiException extends IOException {

    private final int statusCode;
    private final String failureType;

    public HttpClientApiException(String message, int statusCode, String failureType) {
        super(message);
        this.statusCode = statusCode;
        this.failureType = failureType;
    }

    public HttpClientApiException(String message, String failureType, Throwable cause) {
        super(message, cause);
        this.statusCode = -1;
        this.failureType = failureType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getFailureType() {
        return failureType;
    }
}
