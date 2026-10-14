//package gr.cti.nts;
package gr.microbase.gsis.identity.keycloak.mapper;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.http.HttpTimeoutException;
import org.keycloak.broker.provider.AbstractIdentityProviderMapper;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.IdentityProviderSyncMode;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.sessions.AuthenticationSessionModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLException;

import gr.microbase.gsis.identity.keycloak.mapper.Common.HelperMethods;
import gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient.HttpClientApiException;
import gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient.HttpClientPost;
import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecord;
import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecordHelper;
import gr.microbase.gsis.identity.keycloak.mapper.UserMapperModels.GetGovUserByAfmInputRecord;
import gr.microbase.gsis.identity.keycloak.mapper.UserMapperModels.GovUserRequest;
import gr.microbase.gsis.identity.keycloak.mapper.metrics.GsisMetricsService;

import java.util.List;

import org.jboss.logging.Logger;

import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.models.RealmModel;

/**
 * Adds 'gsisTaxId' user attribute retrieved from external API converted
 * (TaxId2UserId).
 *
 * @author george Xiros
 */
public class GsisUserIdProviderMapper extends AbstractIdentityProviderMapper {

    private static final Logger LOG = Logger.getLogger(GsisUserIdProviderMapper.class);
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final String METRIC_MAPPER_NAME = "GsisUserIdProviderMapper";
    private static final String METRIC_ENDPOINT_NAME = "tax_id_converter";
    private final GsisMetricsService gsisMetricsService = new GsisMetricsService();

    public static final String EXELYSIS_SUB_SYSTEM = "Keycloak";

    public static final String CONF_USER_ATTRIBUTE = "userAttribute";

    public static final String UPDATE_USERNAME_WITH_USER_ID = "updateUsernameWithUserId";

    static final String CONF_TAX_CONVERTER_URL = "taxConverterUrl";
    static final String CONF_USERNAME = "username";
    static final String CONF_PASSWORD = "password";

    static final String CONF_CERT_PATH_ATTRIBUTE = "certificatePath";
    static final String CONF_CERT_PASSWORD_ATTRIBUTE = "certificatePassword";
    static final String CONF_APIKEYHEADER_ATTRIBUTE = "apikeyheader";
    static final String CONF_APIKEY_ATTRIBUTE = "apikey";

    public static final String PROVIDER_ID = "custom-attribute-idp-gsis-mapper";

    public static final String[] COMPATIBLE_PROVIDERS = { ANY_PROVIDER };

    @Override
    public String[] getCompatibleProviders() {
        return COMPATIBLE_PROVIDERS;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayCategory() {
        return "Attribute Importer";
    }

    @Override
    public String getDisplayType() {
        return "Tax Id Converter Attribute Mapper";
    }

    @Override
    public String getHelpText() {
        return "Gov Tax Id converter to User Id using an external API converter";
    }

    public void preprocessFederatedIdentity(KeycloakSession session, RealmModel realm,
            IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        var config = mapperModel.getConfig();
        String converterUrl = config.get(CONF_TAX_CONVERTER_URL);
        String username = config.get(CONF_USERNAME);
        String password = config.get(CONF_PASSWORD);
        String attribute = config.get(CONF_USER_ATTRIBUTE);

        if (converterUrl == null || converterUrl.trim().isEmpty()) {
            gsisMetricsService.incrementAuthFailure(METRIC_MAPPER_NAME, "missing_converter_url");
            LOG.warnf("External API converter URL is not set (mapper %s)", mapperModel.getName());
            throw new IdentityBrokerException("Authentication failed: External API converter URL is not set.");
        }

        String taxId;
        try {
            // get Tax id from UserInfo model
            taxId = HelperMethods.GetTaxIdFromUserInfo(context, mapperModel.getName())
                    .trim();
        } catch (IdentityBrokerException e) {
            gsisMetricsService.incrementAuthFailure(METRIC_MAPPER_NAME, "missing_taxid");
            throw e;
        }

        // Get the authentication session
        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        var state = authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM);
        var correlationId = authSession.getClientNote("CorrelationId");
        LOG.debugf(
                "auth-correlation-convert-afm redirect uri: %s, tab id: %s, state: %s, correlation id: %s",
                authSession.getRedirectUri(), authSession.getTabId(), state, correlationId);

        // Create a secure http client if a certificate is used or a simple client
        var apiKey = config.get(CONF_APIKEY_ATTRIBUTE);
        var apiKeyHeader = config.get(CONF_APIKEYHEADER_ATTRIBUTE);
        var httpClient = HttpClientPost.Create(session,
                config.get(CONF_CERT_PATH_ATTRIBUTE),
                config.get(CONF_CERT_PASSWORD_ATTRIBUTE),
                gsisMetricsService,
                METRIC_MAPPER_NAME,
                METRIC_ENDPOINT_NAME);

        String clientType = httpClient.GetClientType();

        // Add headers
        httpClient.AddHeader("Content-type", "application/json; charset=utf-8");
        httpClient.AddHeader("Authorization", HelperMethods.GetAuthorizationValue(username, password));

        if (apiKey != null && !apiKey.isEmpty() &&
                apiKeyHeader != null && !apiKeyHeader.isEmpty()) {

            httpClient.AddHeader(apiKeyHeader, apiKey);
        }

        // Create AuditRecord
        var auditRecord = AuditRecordHelper.CreateAuditRecord(session, mapperModel, correlationId, taxId);

        // Make the request and retrieve a string with userId
        String userId = getUsernameFrom(httpClient, converterUrl, auditRecord, taxId, clientType);

        if (userId == null || userId.trim().isEmpty()) {
            // gsisMetricsService.incrementAuthFailure(METRIC_MAPPER_NAME,
            // "external_api_no_userid");
            HelperMethods.RaiseMapperCustomEvent(session, realm, mapperModel, taxId,
                    "Unable to retrieve userId from external API");
            LOG.warnf("Unable to retrieve userId from external API (mapper %s)", mapperModel.getName());
            throw new IdentityBrokerException("Authentication failed: Unable to retrieve UserId from external API.");
        }

        if (attribute == null || attribute.trim().isEmpty()) {
            gsisMetricsService.incrementAuthFailure(METRIC_MAPPER_NAME, "missing_user_attribute_config");
            LOG.warnf("User Attribute configuration is missing (mapper %s)", mapperModel.getName());
            throw new IdentityBrokerException("Authentication failed: User attribute configuration is missing.");
        }

        context.setUserAttribute(attribute, userId);
        LOG.infof("Context attribute '%s' updated with UserId '%s' (mapper %s) for TaxId '%s'", attribute, userId,
                mapperModel.getName(), taxId);
    }

    public String getUsernameFrom(HttpClientPost httpClient, String apiUrl, AuditRecord auditRecord, String taxId,
            String clientType) {

        String response = null;

        try {
            gsisMetricsService.incrementApiAttempt(METRIC_MAPPER_NAME, METRIC_ENDPOINT_NAME);

            var payload = createRequestPayload(auditRecord, taxId);

            response = httpClient.Send(apiUrl, payload);

            if (response == null || response.trim().isEmpty()) {
                throw new IdentityBrokerException("Empty API response while resolving govUserId.");
            }

            String userId = parseUserIdFromResponse(response);
            return userId;
        } catch (Exception e) {            
            String failureType = classifyFailure(e);
            gsisMetricsService.incrementApiFailure(METRIC_MAPPER_NAME, METRIC_ENDPOINT_NAME, failureType);

            LOG.errorf(e, "Could not retrieve username for tax id: %s from response %s", taxId, response);
            return null;
        }
    }

    private String parseUserIdFromResponse(String response) throws JsonProcessingException {
        JsonNode rootNode = JSON_MAPPER.readTree(response);
        JsonNode responseNode = rootNode.path("getGovUserByAfmOutputRecord").path("response");

        if (!responseNode.isArray()) {
            throw new IdentityBrokerException("API response did not contain a valid govUserId.");
        }

        if (responseNode.isEmpty()) {
            throw new IdentityBrokerException("API response did not contain a valid govUserId.");
        }

        JsonNode usernameNode = responseNode.get(0).path("govUserId");
        if (usernameNode.isMissingNode()) {
            throw new IdentityBrokerException("API response did not contain a valid govUserId.");
        }

        return usernameNode.asText();
    }

    // private static int resolveStatusCode(Exception e, int defaultStatusCode) {
    //     if (e instanceof HttpClientApiException apiException && apiException.getStatusCode() > 0) {
    //         return apiException.getStatusCode();
    //     }

    //     return defaultStatusCode;
    // }

    private static String classifyFailure(Exception e) {
        if (e instanceof HttpClientApiException apiException && apiException.getFailureType() != null) {
            return apiException.getFailureType();
        }
        if (e instanceof HttpTimeoutException || e instanceof SocketTimeoutException
                || e instanceof InterruptedException) {
            return "timeout";
        }
        if (e instanceof SSLException) {
            return "tls";
        }
        if (e instanceof ConnectException || e instanceof UnknownHostException) {
            return "connection";
        }
        if (e instanceof JsonProcessingException) {
            return "parse_error";
        }
        if (e instanceof IdentityBrokerException) {
            String message = e.getMessage();
            if (message != null && message.contains("govUserId")) {
                return "missing_gov_user_id";
            }
            if (message != null && message.contains("Empty API response")) {
                return "empty_response";
            }
        }
        return "unknown";
    }

    private String createRequestPayload(AuditRecord auditRecord, String taxId)
            throws JsonProcessingException {

        GetGovUserByAfmInputRecord inputRecord = new GetGovUserByAfmInputRecord(
                List.of(taxId));

        GovUserRequest request = new GovUserRequest(auditRecord, inputRecord);

        // Convert to JSON
        return request.toJson();
    }

    @Override
    public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user,
            IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        UpdateUserModel(user, mapperModel, context);
    }

    @Override
    public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user,
            IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        UpdateUserModel(user, mapperModel, context);
    }

    private void UpdateUserModel(UserModel user, IdentityProviderMapperModel mapperModel,
            BrokeredIdentityContext context) {
        var attribute = mapperModel.getConfig().get(CONF_USER_ATTRIBUTE);
        if (attribute == null || attribute.trim().isEmpty()) {
            LOG.warnf("User Attribute is not configured (mapper %s)", mapperModel.getName());
            return;
        }

        var userId = context.getUserAttribute(attribute);
        if (userId == null || userId.trim().isEmpty()) {
            return;
        }

        boolean updateUsernameWithUserId = Boolean
                .parseBoolean(mapperModel.getConfig().get(UPDATE_USERNAME_WITH_USER_ID));

        user.setSingleAttribute(attribute, userId);
        LOG.infof("User attribute '%s' updated with UserId '%s' (mapper %s)", attribute, userId, mapperModel.getName());

        if (updateUsernameWithUserId) {
            user.setUsername(userId);
            LOG.infof("Username updated with UserId '%s' (mapper %s)", userId, mapperModel.getName());
        }
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return GsisUserIdProviderMapperConfig.getConfigProperties();
    }

    @Override
    public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
        // Explicitly declare support for FORCE sync mode
        return syncMode == IdentityProviderSyncMode.FORCE || syncMode == IdentityProviderSyncMode.IMPORT;
    }
}