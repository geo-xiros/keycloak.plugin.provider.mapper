package gr.microbase.gsis.identity.keycloak.mapper.UserMapperModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecord;

/**
 * The root request object containing audit information and request data
 */
public class GovUserRequest {
    @JsonProperty("auditRecord")
    private AuditRecord auditRecord;
    
    @JsonProperty("getGovUserByAfmInputRecord")
    private GetGovUserByAfmInputRecord getGovUserByAfmInputRecord;
    
    // Default constructor
    public GovUserRequest() {
    }
    
    // Constructor with all fields
    public GovUserRequest(AuditRecord auditRecord, GetGovUserByAfmInputRecord getGovUserByAfmInputRecord) {
        this.auditRecord = auditRecord;
        this.getGovUserByAfmInputRecord = getGovUserByAfmInputRecord;
    }
    
    // Getters and Setters
    public AuditRecord getAuditRecord() {
        return auditRecord;
    }
    
    public void setAuditRecord(AuditRecord auditRecord) {
        this.auditRecord = auditRecord;
    }
    
    public GetGovUserByAfmInputRecord getGetGovUserByAfmInputRecord() {
        return getGovUserByAfmInputRecord;
    }
    
    public void setGetGovUserByAfmInputRecord(GetGovUserByAfmInputRecord getGovUserByAfmInputRecord) {
        this.getGovUserByAfmInputRecord = getGovUserByAfmInputRecord;
    }
    
    public void setIncludeExtraFields(boolean includeExtraFields) {
        if (this.auditRecord != null) {
            this.auditRecord.setIncludeExtraFields(includeExtraFields);
        }
    }
    
    /**
     * Converts this object to a JSON string
     * @return JSON representation of this object
     * @throws JsonProcessingException if there's an error during JSON conversion
     */
    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Configure to handle date/time properly
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper.writeValueAsString(this);
    }
    
    /**
     * Creates a GovUserRequest object from a JSON string
     * @param json JSON string to parse
     * @return GovUserRequest object
     * @throws JsonProcessingException if there's an error during JSON parsing
     */
    public static GovUserRequest fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Configure to handle date/time properly
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper.readValue(json, GovUserRequest.class);
    }
}