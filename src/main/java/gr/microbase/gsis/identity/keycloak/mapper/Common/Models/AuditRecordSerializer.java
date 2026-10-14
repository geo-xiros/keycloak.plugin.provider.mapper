package gr.microbase.gsis.identity.keycloak.mapper.Common.Models;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Custom serializer for AuditRecord to conditionally include fields based on includeExtraFields flag
 */
class AuditRecordSerializer extends JsonSerializer<AuditRecord> {
    @Override
    public void serialize(AuditRecord auditRecord, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
        gen.writeStartObject();
        
        // Only include these fields if the flag is set
        if (auditRecord.isIncludeExtraFields()) {
            gen.writeStringField("auditParentCorrelationId", auditRecord.getAuditParentCorrelationId());
            gen.writeStringField("exelysisSubsystem", auditRecord.getExelysisSubsystem());
        }
        
        // Always include these fields
        gen.writeStringField("auditTransactionId", auditRecord.getAuditTransactionId());
        gen.writeStringField("auditTransactionDate", auditRecord.getAuditTransactionDate());
        gen.writeStringField("auditUnit", auditRecord.getAuditUnit());
        gen.writeStringField("auditProtocol", auditRecord.getAuditProtocol());
        gen.writeStringField("auditUserId", auditRecord.getAuditUserId());
        gen.writeStringField("auditUserIp", auditRecord.getAuditUserIp());
        
        gen.writeEndObject();
    }
}