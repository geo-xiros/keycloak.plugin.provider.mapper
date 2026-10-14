package gr.microbase.gsis.identity.keycloak.mapper.UserMapperModels;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Input data containing AFM (Tax ID) list for querying user information
 */
public class GetGovUserByAfmInputRecord {
    @JsonProperty("afmList")
    private List<String> afmList;
    
    // Default constructor
    public GetGovUserByAfmInputRecord() {
    }
    
    // Constructor with fields
    public GetGovUserByAfmInputRecord(List<String> afmList) {
        this.afmList = afmList;
    }
    
    // Getters and Setters
    public List<String> getAfmList() {
        return afmList;
    }
    
    public void setAfmList(List<String> afmList) {
        this.afmList = afmList;
    }
}
