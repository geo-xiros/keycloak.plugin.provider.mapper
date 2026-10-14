//package gr.cti.nts;
package gr.microbase.gsis.identity.keycloak.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels.EmployeeAuthorisation;
import gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels.GovRolesRequest;
import gr.microbase.gsis.identity.keycloak.mapper.Common.HelperMethods;
import gr.microbase.gsis.identity.keycloak.mapper.Common.HttpClient.HttpClientPost;
import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecord;
import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecordHelper;

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
public class GsisAuthorizationsProviderMapper extends AbstractIdentityProviderMapper {

    public static final Logger LOG = Logger.getLogger(GsisAuthorizationsProviderMapper.class);

    public static final String EXELYSIS_SUB_SYSTEM = "Keycloak";

    public static final String CONF_ROLES_ATTRIBUTE = "rolesAttribute";
    public static final String CONF_ROLES_MAPPING = "rolesMapping";

    static final String CONF_ROLES_PROVIDER_URL = "rolesProviderUrl";
    static final String CONF_USERNAME = "username";
    static final String CONF_PASSWORD = "password";

    static final String CONF_CERT_PATH_ATTRIBUTE = "certificatePath";
    static final String CONF_CERT_PASSWORD_ATTRIBUTE = "certificatePassword";
    static final String CONF_APIKEYHEADER_ATTRIBUTE = "apikeyheader";
    static final String CONF_APIKEY_ATTRIBUTE = "apikey";

    public static final String PROVIDER_ID = "custom-attribute-authorizations-gsis-mapper";

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
        return "User authorizations provider Attribute Mapper";
    }

    @Override
    public String getHelpText() {
        return "provides user roles from Tax Id using an external API";
    }

    public void preprocessFederatedIdentity(KeycloakSession session, RealmModel realm,
            IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        String rolesProviderUrl = mapperModel.getConfig().get(CONF_ROLES_PROVIDER_URL);
        String username = mapperModel.getConfig().get(CONF_USERNAME);
        String password = mapperModel.getConfig().get(CONF_PASSWORD);
        String attribute = mapperModel.getConfig().get(CONF_ROLES_ATTRIBUTE);

        AuthenticationSessionModel authSession = context.getAuthenticationSession();
        if (authSession == null) {
            return; // happens in some flows
        }

        String scope = authSession.getClientNote("scope");
        if (scope == null || !scope.contains("read-roles")) {
            LOG.debugf("Required scope 'read-roles', actual scope: '%s'", scope);
            return;
        }

        if (rolesProviderUrl == null || rolesProviderUrl.trim().isEmpty()) {
            LOG.warnf("External Role provider URL is not set (mapper %s)", mapperModel.getName());
            return;
            // throw new IdentityBrokerException("Authentication failed: External API
            // converter URL is not set.");
        }
        if (username == null || username.trim().isEmpty()) {
            LOG.warnf("External Role provider username is not set (mapper %s)", mapperModel.getName());
            return;
        }

        // get Tax id from UserInfo model
        var taxId = HelperMethods.GetTaxIdFromUserInfo(context, mapperModel.getName())
                .trim();

        // Get the authentication session
        var state = authSession.getClientNote(OIDCLoginProtocol.STATE_PARAM);
        var correlationId = authSession.getClientNote("CorrelationId");
        LOG.debugf("auth-correlation-convert-afm redirect uri: %s, tab id: %s, state: %s, correlation id: %s",
                authSession.getRedirectUri(), authSession.getTabId(), state, correlationId);

        // Create a secure http client if a certificate is used or a simple client
        var apiKey = mapperModel.getConfig().get(CONF_APIKEY_ATTRIBUTE);
        var apiKeyHeader = mapperModel.getConfig().get(CONF_APIKEYHEADER_ATTRIBUTE);
        var httpClient = HttpClientPost.Create(session,
                mapperModel.getConfig().get(CONF_CERT_PATH_ATTRIBUTE),
                mapperModel.getConfig().get(CONF_CERT_PASSWORD_ATTRIBUTE));

        // Add headers
        httpClient.AddHeader("Content-type", "application/json; charset=utf-8");
        httpClient.AddHeader("Authorization", HelperMethods.GetAuthorizationValue(username, password));

        if (apiKey != null && !apiKey.isEmpty() &&
                apiKeyHeader != null && !apiKeyHeader.isEmpty()) {

            httpClient.AddHeader(apiKeyHeader, apiKey);
        }

        // Create AuditRecord
        var auditRecord = AuditRecordHelper.CreateAuditRecord(session, mapperModel, correlationId, taxId);

        // Make the request and retrieve a string with delimeted role ids
        String userRoles = getUserRolesFrom(httpClient, rolesProviderUrl, auditRecord, taxId);

        if (userRoles == null || userRoles.trim().isEmpty()) {
            HelperMethods.RaiseMapperCustomEvent(session, realm, mapperModel, taxId,
                    "Unable to retrieve user roles from external API");
            LOG.warnf("Unable to retrieve roles from external provider API (mapper %s)", mapperModel.getName());
            throw new IdentityBrokerException(
                    "Authentication failed: Unable to retrieve user roles from external API.");
            // return;
        }

        if (attribute == null || attribute.trim().isEmpty()) {
            LOG.warnf("Role Attribute configuration is missing (mapper %s)", mapperModel.getName());
            return;
            // throw new IdentityBrokerException("Authentication failed: Role attribute
            // configuration is missing.");
        }

        context.setUserAttribute(attribute, userRoles);
        LOG.debugf("Context attribute '%s' updated with Roles '%s' (mapper %s) for TaxId '%s'", attribute, userRoles,
                mapperModel.getName(), taxId);
    }

    public String getUserRolesFrom(HttpClientPost httpClient, String rolesProviderUrl, AuditRecord auditRecord,
            String taxId) {

        String response = null;

        try {
            var payload = createRequestPayload(auditRecord, taxId);

            response = httpClient.Send(rolesProviderUrl, payload);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode employeesNode = rootNode.path("padEmplListOutputRecord")
                    .path("pageModel")
                    .path("pubAuthDoc")
                    .path("employeesList")
                    .path("employees");

            if (employeesNode.isMissingNode()) {
                throw new IdentityBrokerException("API response did not contain any valid userAuthorizations.");
            }

            if (!employeesNode.isArray() || employeesNode.size() == 0) {
                LOG.debugf("No employee was found with '%s' tax id", taxId);
                return "";
            }

            var firstEmployeeNode = employeesNode.get(0);
            JsonNode authorisationsArrayNode = firstEmployeeNode.path("authorisations");

            // Convert to list of EmployeeAuthorisation
            List<EmployeeAuthorisation> authorisations = mapper
                    .readerForListOf(EmployeeAuthorisation.class)
                    .readValue(authorisationsArrayNode);

            return authorisations.stream()
                    .filter(a -> a.Active)
                    .map(a -> String.valueOf(a.Role.RoleId))
                    .collect(Collectors.joining(","));
        } catch (Exception e) {
            LOG.errorf(e, "Could not retrieve user roles for tax id: %s from response %s", taxId, response);
            return null;
            // throw new IdentityBrokerException(
            // "Authentication failed: Unable to retrieve user roles from external API.",
            // e);
        }
    }

    private String createRequestPayload(AuditRecord auditRecord, String taxId)
            throws JsonProcessingException {

        GovRolesRequest request = new GovRolesRequest(auditRecord, taxId.trim());

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

    private void UpdateUserModel(UserModel user,
            IdentityProviderMapperModel mapperModel,
            BrokeredIdentityContext context) {
        var attribute = mapperModel.getConfig().get(CONF_ROLES_ATTRIBUTE);
        if (attribute == null || attribute.trim().isEmpty()) {
            LOG.warnf("Roles Attribute is not configured (mapper %s)", mapperModel.getName());
            return;
        }

        var roles = context.getUserAttribute(attribute);
        if (roles == null || roles.trim().isEmpty()) {
            return;
        }

        user.setSingleAttribute(attribute, roles);
        LOG.infof("Roles Attribute '%s' updated with UserId '%s' (mapper %s)", attribute, roles, mapperModel.getName());

        var rolesMapping = mapperModel.getConfig().get(CONF_ROLES_MAPPING);
        if (rolesMapping == null || rolesMapping.isBlank()) {
            LOG.warnf("Roles Mapping is not configured (mapper %s)", mapperModel.getName());
            return;
        }

        LOG.infof("Roles Mapping '%s' retrieved (mapper %s)", rolesMapping, mapperModel.getName());

        Map<String, String> roleMapping = convertRolesMappingToMap(rolesMapping);

        // The conversion is now handled by convertRolesMappingToMap, so this block can
        // be removed.
        List<String> appRoles = convertRolesToAppRoles(roles, roleMapping);

        user.setAttribute("mappedRoles", appRoles);
    }

    private static Map<String, String> convertRolesMappingToMap(String rolesMapping) {
        return java.util.Arrays.stream(rolesMapping.split("\\R"))
                .map(entry -> entry.split("="))
                .filter(entry -> entry.length == 2)
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1].trim()));
    }

    private static List<String> convertRolesToAppRoles(String roles, Map<String, String> roleMapping) {
        return java.util.Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(roleMapping::containsKey)
                .map(roleMapping::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return GsisAuthorizationsProviderMapperConfig.getConfigProperties();
    }

    @Override
    public boolean supportsSyncMode(IdentityProviderSyncMode syncMode) {
        // Explicitly declare support for FORCE sync mode
        return syncMode == IdentityProviderSyncMode.FORCE || syncMode == IdentityProviderSyncMode.IMPORT;
    }
}