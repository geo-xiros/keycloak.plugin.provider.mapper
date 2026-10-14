package gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.Map;

import javax.net.ssl.SSLContext;

public final class SecureHttpClientPost extends HttpClientPost{

    private static final int REQUEST_TIMEOUT_SECONDS = 30;

    private HttpClient secureHttp;

    public SecureHttpClientPost(SSLContext sslContext ){
        this.ClientType = "secure";
        secureHttp = HttpClient.newBuilder()
            .sslContext(sslContext)
            .connectTimeout(Duration.ofSeconds(REQUEST_TIMEOUT_SECONDS))
            .version(HttpClient.Version.HTTP_2) // Use HTTP/2 for better performance
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();
    }
    
    @Override
    protected String SendInternal(String apiUrl, String payload) throws IOException, InterruptedException{
        
        var requestBuilder = HttpRequest.newBuilder().uri(URI.create(apiUrl));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.header(entry.getKey(), entry.getValue());
        }

        HttpRequest request = requestBuilder.POST(HttpRequest.BodyPublishers.ofString(payload)).build();

        try {
            HttpResponse<String> httpResponse = secureHttp.send(request, HttpResponse.BodyHandlers.ofString());
            SetLastStatusCode(httpResponse.statusCode());

            if (httpResponse.statusCode() != 200) {
                throw new HttpClientApiException(
                        "API response failed with status code: " + httpResponse.statusCode(),
                        httpResponse.statusCode(),
                        "http_non_200");
            }

            return httpResponse.body();
        } catch (HttpTimeoutException e) {
            throw new HttpClientApiException("Timeout while calling API: " + apiUrl, "timeout", e);
        }
    }
}