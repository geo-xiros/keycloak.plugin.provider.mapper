package gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.entity.StringEntity;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.models.KeycloakSession;

public final class SimpleHttpClientPost extends HttpClientPost{
    
    private KeycloakSession session;

    public SimpleHttpClientPost(KeycloakSession session){
        this.session = session;
        this.ClientType = "simple";
    }
    
    @Override
    protected String SendInternal(String apiUrl, String payload) throws IOException, InterruptedException{
        
        // Make an API call to retrieve the username using the taxid parameter
        SimpleHttp request = SimpleHttp.doPost(apiUrl, session)
                .entity(new StringEntity(payload, StandardCharsets.UTF_8));

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            request.header(entry.getKey(), entry.getValue());
        }

        // Parse the API response
        String response = request.asString();
        SetLastStatusCode(200);
        return response;
    }
}