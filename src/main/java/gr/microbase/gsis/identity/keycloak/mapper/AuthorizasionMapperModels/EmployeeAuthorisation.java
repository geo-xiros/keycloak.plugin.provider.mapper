package gr.microbase.gsis.identity.keycloak.mapper.AuthorizasionMapperModels;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeAuthorisation {
    @JsonProperty("active") public boolean Active;    
    @JsonProperty("role") public Role Role;    
    @JsonProperty("organisation") public UserOrganisation Organisation;
}

//{"padEmplListOutputRecord":{
//   "pageModel" : {
//     "pubAuthDoc" : {
//       "employeesList" : {
//         "employees" : [ {
//           "employeeId" : 1946,
//           "employeeVatNo" : "062338540",
//           "employeeName" : "Όνομα Υπαλ",
//           "employeeSurname" : "ΕΠΩΝΥΜΟ",
//           "authorisations" : [ {
//             "id" : 4336,
//             "startDate" : "2025-09-29",
//             "role" : {
//               "roleId" : 2200,
//               "hid" : 2188
//             },
//             "userOrgVatNo" : "997001671",
//             "active" : true,
//             "userOrgCode" : "66797"
//           }, {
//             "id" : 2976,
//             "startDate" : "2024-07-02",
//             "endDate" : "2025-02-03",
//             "role" : {
//               "roleId" : 1660,
//               "hid" : 2188
//             },
//             "userOrgVatNo" : "997001671",
//             "active" : false,
//             "userOrgCode" : "66797"
//           }, {
//             "id" : 3411,
//             "startDate" : "2025-02-03",
//             "endDate" : "2025-09-29",
//             "role" : {
//               "roleId" : 2202,
//               "hid" : 2188
//             },
//             "userOrgVatNo" : "997001671",
//             "active" : false,
//             "userOrgCode" : "66797"
//           } ],
//           "dectivateInITSystem" : false
//         } ]
//       }
//     }
//   },
//   "pageData" : { }
// },"callSequenceId":10047351072,"callSequenceDate":"2025-11-03T17:08:28.987+02:00","errorRecord":{}}
//: com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot deserialize value of type `java.util.ArrayList<gr.microbase.gsis.identity.keycloak.mapper.EmployeeAuthorisation>` from Object value (token `JsonToken.START_OBJECT`)
