package gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserOrganisation {
    @JsonProperty("organisationUnitCode") public String OrganisationUnitCode;
    @JsonProperty("organisationUnitName") public String OrganisationUnitName;
}