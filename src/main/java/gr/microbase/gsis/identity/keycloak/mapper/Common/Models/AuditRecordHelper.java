package gr.microbase.gsis.identity.keycloak.mapper.Common.Models;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;


import gr.microbase.gsis.identity.keycloak.mapper.Common.HelperMethods;

public class AuditRecordHelper{

    public static final String CONF_AUDIT_PROTOCOL = "auditProtocol";
    public static final String CONF_AUDIT_SUB_SYSTEM = "exelysisSubsystem";
    public static final String CONF_AUDIT_UNIT = "auditUnit";
    public static final String CONF_AUDIT_USER_ID = "auditUserId";

    public static AuditRecord CreateAuditRecord(KeycloakSession session, IdentityProviderMapperModel mapperModel, String correlationId, String auditUserId){
        String auditProtocol = mapperModel.getConfig().get(CONF_AUDIT_PROTOCOL);
        String auditSubSystem = mapperModel.getConfig().get(CONF_AUDIT_SUB_SYSTEM);
        String auditUnit = mapperModel.getConfig().get(CONF_AUDIT_UNIT);
        
        if (auditUserId == null || auditUserId.trim().isEmpty()) {
            auditUserId = mapperModel.getConfig().get(CONF_AUDIT_USER_ID);
        }

        var ipAddress = HelperMethods.GetClientIpAddress(session);

        return AuditRecordHelper.CreateAuditRecord(ipAddress, correlationId, auditSubSystem, auditUnit, auditProtocol, auditUserId, true);
    }

    private static AuditRecord CreateAuditRecord(
        String ipAddress, 
        String correlationId, 
        String auditSubSystem, 
        String auditUnit,
        String auditProtocol, 
        String auditUserId,
        boolean includeExtraAuditFields) {

        // Get current date-time in UTC
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneOffset.UTC);

        // Define custom formatter without milliseconds
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

        // Create a new request object
        AuditRecord auditRecord = new AuditRecord(
                correlationId,
                auditSubSystem,
                null, // always send null id so that the audit middleware generates a new one everytime (UUID.randomUUID().toString()),
                utcDateTime.format(formatter), // Format the date-time as a string
                auditUnit,
                auditProtocol,
                auditUserId,
                ipAddress);

        auditRecord.setIncludeExtraFields(includeExtraAuditFields);

        return auditRecord;
    }
   
}