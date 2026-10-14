package gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class GovRolesRequest {

    public class Employee {
        @JsonProperty("employeeVatNo")        public String EmployeeVatNo;
    }

    public class Source {
        @JsonProperty("employee")        public Employee Employee;
    }

    public class PadEmplListInputRecord {
        @JsonProperty("page")        public String Page;
        @JsonProperty("size")        public String Size;
        @JsonProperty("lang")        public String Lang;
        @JsonProperty("source")        public Source Source;
    }

    @JsonProperty("auditRecord")
    public gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecord AuditRecord;

    @JsonProperty("padEmplListInputRecord")
    public PadEmplListInputRecord PadEmplListInputRecord;

    public GovRolesRequest(gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecord auditRecord, String userTaxId) {
   
        Employee employee = new Employee();
        employee.EmployeeVatNo = userTaxId;

        Source source = new Source();
        source.Employee = employee;

        PadEmplListInputRecord padInput = new PadEmplListInputRecord();
        padInput.Page = "1";
        padInput.Size = "1";
        padInput.Lang = "el";
        padInput.Source = source;
        
        AuditRecord = auditRecord;
        PadEmplListInputRecord = padInput;
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
}