package gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Role {
    @JsonProperty("roleId") public int RoleId;
    @JsonProperty("roleName") public String RoleName;
    @JsonProperty("hid") public int Hid;
}