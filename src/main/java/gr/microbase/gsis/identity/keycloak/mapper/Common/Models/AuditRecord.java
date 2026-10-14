package gr.microbase.gsis.identity.keycloak.mapper.Common.Models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Audit information for tracking and logging purposes
 */
@JsonSerialize(using = AuditRecordSerializer.class)
public class AuditRecord {
    private String auditParentCorrelationId;
    private String exelysisSubsystem;
    private String auditTransactionId;
    private String auditTransactionDate;
    private String auditUnit;
    private String auditProtocol;
    private String auditUserId;
    private String auditUserIp;
    
    // Control field for JSON serialization (not serialized itself)
    private transient boolean includeExtraFields = true;
    
    // Default constructor
    public AuditRecord() {
    }
    
    // Constructor with all fields
    public AuditRecord(String auditParentCorrelationId, String exelysisSubsystem, String auditTransactionId,
                      String auditTransactionDate, String auditUnit, String auditProtocol,
                      String auditUserId, String auditUserIp) {
        this.auditParentCorrelationId = auditParentCorrelationId;
        this.exelysisSubsystem = exelysisSubsystem;
        this.auditTransactionId = auditTransactionId;
        this.auditTransactionDate = auditTransactionDate;
        this.auditUnit = auditUnit;
        this.auditProtocol = auditProtocol;
        this.auditUserId = auditUserId;
        this.auditUserIp = auditUserIp;
    }
    
    // Getters and Setters
    public String getAuditParentCorrelationId() {
        return auditParentCorrelationId;
    }
    
    public void setAuditParentCorrelationId(String auditParentCorrelationId) {
        this.auditParentCorrelationId = auditParentCorrelationId;
    }
    
    public String getExelysisSubsystem() {
        return exelysisSubsystem;
    }
    
    public void setExelysisSubsystem(String exelysisSubsystem) {
        this.exelysisSubsystem = exelysisSubsystem;
    }
    
    public String getAuditTransactionId() {
        return auditTransactionId;
    }
    
    public void setAuditTransactionId(String auditTransactionId) {
        this.auditTransactionId = auditTransactionId;
    }
    
    public String getAuditTransactionDate() {
        return auditTransactionDate;
    }
    
    public void setAuditTransactionDate(String auditTransactionDate) {
        this.auditTransactionDate = auditTransactionDate;
    }
    
    public String getAuditUnit() {
        return auditUnit;
    }
    
    public void setAuditUnit(String auditUnit) {
        this.auditUnit = auditUnit;
    }
    
    public String getAuditProtocol() {
        return auditProtocol;
    }
    
    public void setAuditProtocol(String auditProtocol) {
        this.auditProtocol = auditProtocol;
    }
    
    public String getAuditUserId() {
        return auditUserId;
    }
    
    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }
    
    public String getAuditUserIp() {
        return auditUserIp;
    }
    
    public void setAuditUserIp(String auditUserIp) {
        this.auditUserIp = auditUserIp;
    }
    
    public boolean isIncludeExtraFields() {
        return includeExtraFields;
    }
    
    public void setIncludeExtraFields(boolean includeExtraFields) {
        this.includeExtraFields = includeExtraFields;
    }
}