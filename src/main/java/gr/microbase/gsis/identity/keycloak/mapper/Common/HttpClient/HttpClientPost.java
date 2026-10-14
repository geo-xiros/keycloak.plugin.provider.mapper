package gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.models.KeycloakSession;

import gr.microbase.gsis.identity.keycloak.mapper.metrics.GsisMetricsService;

public abstract class HttpClientPost{
    
    private static final Logger LOG = Logger.getLogger(HttpClientPost.class);
    
    protected String ClientType;
    protected int lastStatusCode = -1;

    protected Map<String, String> headers = new HashMap<String, String>();

    public void AddHeader(String key, String value){
        headers.put(key, value);
    }

    public String GetClientType() {
        return ClientType == null || ClientType.isBlank() ? "unknown" : ClientType;
    }

    public int GetLastStatusCode() {
        return lastStatusCode;
    }

    protected void SetLastStatusCode(int statusCode) {
        this.lastStatusCode = statusCode;
    }
    
    public String Send(String apiUrl, String payload) throws IOException, InterruptedException{
        
        LOG.debugf("%s to '%s' with payload: %s", this.getClass().getSimpleName(), apiUrl, payload);
        
        var response = SendInternal(apiUrl, payload);
        
        LOG.debugf("Request to: '%s' responded with: %s", apiUrl, response);
        
        return response;
    }

    protected abstract String SendInternal(String apiUrl, String payload) throws IOException, InterruptedException;

    public static HttpClientPost Create(KeycloakSession session, String certPath, String certPassword){
        return Create(session, certPath, certPassword, null, null, null);
    }

    public static HttpClientPost Create(KeycloakSession session, String certPath, String certPassword,
            GsisMetricsService metricsService, String mapperName, String endpointName){
        
        if (certPath == null || certPath.isEmpty() || 
            certPassword == null || certPassword.isEmpty()) {

            LOG.debug("Simple http client was successfully created");

            return new SimpleHttpClientPost(session);
        }

        try {
            // Create SSL context with proper TLS configuration
            SSLContext sslContext = createSSLContext(certPath, certPassword);
            var secureClient=new SecureHttpClientPost(sslContext);
            
            LOG.debug("Secure http client was successfully created");

            
            return secureClient;
        } catch (Exception e) {
            LOG.error("Failed to create a secure http client", e);

            return new SimpleHttpClientPost(session);
        }  
    }

    /**
     * Creates SSL context with proper TLS configuration
     */
    private static SSLContext createSSLContext(String certPath, String certPassword) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.3");

        // Load PFX certificate file
        KeyStore keyStore = KeyStore.getInstance("PKCS12"); // PFX files are PKCS12 format
        char[] password = certPassword.toCharArray();

        try (FileInputStream pfxStream = new FileInputStream(certPath)) {
            keyStore.load(pfxStream, password);
        } catch (IOException e) {
            throw new IdentityBrokerException("PFX certificate file not found: " + certPath);
        }

        // Initialize key manager with the PFX certificate
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password); // Same password for keystore and private key in PFX
        
        // Initialize SSL context (using default trust managers for server certificate
        // validation)
        sslContext.init(keyManagerFactory.getKeyManagers(), // Your client certificate from PFX
                null, // Use default trust managers (works for most APIs)
                new SecureRandom());

        return sslContext;
    }

}