package gr.microbase.gsis.identity.keycloak.mapper.Common;

import java.util.Base64;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import com.fasterxml.jackson.databind.JsonNode;

import gr.microbase.gsis.identity.keycloak.mapper.GsisAuthorizationsProviderMapper;

import org.keycloak.events.EventType;

public class HelperMethods{

    private static final Logger LOG = Logger.getLogger(HelperMethods.class);


    public static void RaiseMapperCustomEvent(KeycloakSession session, RealmModel realm,
                IdentityProviderMapperModel mapperModel, String taxId, String errorString) {
        EventBuilder eventBuilder = new EventBuilder(realm, session, session.getContext().getConnection());
        eventBuilder.event(EventType.CUSTOM_REQUIRED_ACTION_ERROR)
            .detail("mapper", mapperModel.getName())
            .detail("taxId", taxId)
            .error(errorString);
    }

    public static String GetAuthorizationValue(String username, String password){
        // Basic Authentication credentials
        String auth = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
    
    public static String GetTaxIdFromUserInfo(BrokeredIdentityContext context, String mapperModelName) {    

         // Extract raw JSON profile from context
        Object userInfoJson = context.getContextData().get("UserInfo");
        if (!(userInfoJson instanceof JsonNode)) {
            LOG.warnf("Unable to retrieve UserInfo from BrokeredIdentityContext: %s (mapper %s)", context, mapperModelName);
            throw new IdentityBrokerException("Authentication failed: Unable to retrieve UserInfo from BrokeredIdentityContext.");
        }

        var taxId = HelperMethods.GetJsonNodeFieldValue((JsonNode) userInfoJson, "taxid");
        if (taxId == null || taxId.trim().isEmpty()) {
            LOG.warnf("TaxId is missing in UserInfo: %s (mapper %s)", userInfoJson, mapperModelName);
            throw new IdentityBrokerException("Authentication failed: TaxId is missing in UserInfo.");
        }

        return taxId;
    }


    public static String GetClientIpAddress(KeycloakSession session) {

        KeycloakContext context = session.getContext();
        
        // Check X-Forwarded-For header first (common for proxies)
        String ip = context.getRequestHeaders().getHeaderString("X-Real-IP");
        var from = "X-Real-IP";

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = context.getRequestHeaders().getHeaderString("X-Forwarded-For");
            from = "X-Forwarded-For";
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = context.getRequestHeaders().getHeaderString("Proxy-Client-IP");
            from = "Proxy-Client-IP";
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = context.getRequestHeaders().getHeaderString("WL-Proxy-Client-IP");
            from = "WL-Proxy-Client-IP";
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = context.getRequestHeaders().getHeaderString("HTTP_CLIENT_IP");
            from = "HTTP_CLIENT_IP";
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = context.getRequestHeaders().getHeaderString("HTTP_X_FORWARDED_FOR");
            from = "HTTP_X_FORWARDED_FOR";
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            from = "unknown";
            return "0.0.0.0";
        }

        GsisAuthorizationsProviderMapper.LOG.debugf("found ip '%s' from '%s", ip, from);

        // If X-Forwarded-For contains multiple IPs, the first one is typically the
        // client's
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    public static String GetJsonNodeFieldValue(JsonNode rootNode, String fieldName) {
        try {
            // Navigate to the field node
            JsonNode fieldNode = rootNode.get(fieldName);

            // Return the value if it exists
            if (fieldNode != null) {
                return fieldNode.asText();
            } else {
                return null; // field not found
            }
        } catch (Exception e) {
            LOG.errorf(e, "Failed to get '%s' field from JsonNode", fieldName);
            return null; // Handle parsing error
        }
    }
}