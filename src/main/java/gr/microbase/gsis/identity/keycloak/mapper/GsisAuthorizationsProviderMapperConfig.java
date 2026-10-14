package gr.microbase.gsis.identity.keycloak.mapper;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.provider.ProviderConfigProperty;

import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecordHelper;

public class GsisAuthorizationsProviderMapperConfig {
    public static List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> configProperties = new ArrayList<>();

        // Define the property for the audit protocol
        ProviderConfigProperty converterAuditProtocolProperty = new ProviderConfigProperty();
        converterAuditProtocolProperty.setName(AuditRecordHelper.CONF_AUDIT_PROTOCOL);
        converterAuditProtocolProperty.setLabel("Audit Protocol value");
        converterAuditProtocolProperty
                .setHelpText("Enter the audit protocol value to use in the Audit Record payload request.");
        converterAuditProtocolProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterAuditProtocolProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty converterAuditSubSystemProperty = new ProviderConfigProperty();
        converterAuditSubSystemProperty.setName(AuditRecordHelper.CONF_AUDIT_SUB_SYSTEM);
        converterAuditSubSystemProperty.setLabel("Audit Exelysis Sub System value");
        converterAuditSubSystemProperty
                .setHelpText(
                        "Enter the audit exelysis sub system value to use in the Audit Record payload request.");
        converterAuditSubSystemProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterAuditSubSystemProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty converterAuditUnitProperty = new ProviderConfigProperty();
        converterAuditUnitProperty.setName(AuditRecordHelper.CONF_AUDIT_UNIT);
        converterAuditUnitProperty.setLabel("Audit Unit value");
        converterAuditUnitProperty
                .setHelpText("Enter the audit unit value to use in the Audit Record payload request.");
        converterAuditUnitProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterAuditUnitProperty);

        // Define the property for the audit user id
        ProviderConfigProperty converterAuditUserIdProperty = new ProviderConfigProperty();
        converterAuditUserIdProperty.setName(AuditRecordHelper.CONF_AUDIT_USER_ID);
        converterAuditUserIdProperty.setLabel("Audit User Id value");
        converterAuditUserIdProperty
                .setHelpText("Enter the audit user id value to use in the Audit Record payload request.");
        converterAuditUserIdProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterAuditUserIdProperty);

        var attributeNameProperty = new ProviderConfigProperty();
        attributeNameProperty.setName(GsisAuthorizationsProviderMapper.CONF_ROLES_ATTRIBUTE);
        attributeNameProperty.setLabel("Roles Attribute Name");
        attributeNameProperty.setHelpText("Roles attribute name to store information into.");
        attributeNameProperty.setType(ProviderConfigProperty.USER_PROFILE_ATTRIBUTE_LIST_TYPE);
        configProperties.add(attributeNameProperty);

        // Define the property for the converter url
        ProviderConfigProperty converterUrlProperty = new ProviderConfigProperty();
        converterUrlProperty.setName(GsisAuthorizationsProviderMapper.CONF_ROLES_PROVIDER_URL);
        converterUrlProperty.setLabel("Roles provider API Url");
        converterUrlProperty.setHelpText("Enter the external API Url used to retrieve user roles by Tax Id.");
        converterUrlProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterUrlProperty);

        // Define the property for the username
        ProviderConfigProperty usernameProperty = new ProviderConfigProperty();
        usernameProperty.setName(GsisAuthorizationsProviderMapper.CONF_USERNAME);
        usernameProperty.setLabel("API username");
        usernameProperty.setHelpText("Enter external API username.");
        usernameProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(usernameProperty);

        // Define the property for the password
        ProviderConfigProperty passwordProperty = new ProviderConfigProperty();
        passwordProperty.setName(GsisAuthorizationsProviderMapper.CONF_PASSWORD);
        passwordProperty.setLabel("API password");
        passwordProperty.setHelpText("Enter external API password.");
        passwordProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(passwordProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty certificationPathProperty = new ProviderConfigProperty();
        certificationPathProperty.setName(GsisAuthorizationsProviderMapper.CONF_CERT_PATH_ATTRIBUTE);
        certificationPathProperty.setLabel("Audit middleware certification path");
        certificationPathProperty
                .setHelpText("Enter the certification path used for Audit middleware request.");
        certificationPathProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(certificationPathProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty certificationPasswordProperty = new ProviderConfigProperty();
        certificationPasswordProperty.setName(GsisAuthorizationsProviderMapper.CONF_CERT_PASSWORD_ATTRIBUTE);
        certificationPasswordProperty.setLabel("Audit middleware certification password");
        certificationPasswordProperty
                .setHelpText("Enter the certification password used for Audit middleware request.");
        certificationPasswordProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(certificationPasswordProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty apikeyHeaderProperty = new ProviderConfigProperty();
        apikeyHeaderProperty.setName(GsisAuthorizationsProviderMapper.CONF_APIKEYHEADER_ATTRIBUTE);
        apikeyHeaderProperty.setLabel("Audit middleware certification Api key header name");
        apikeyHeaderProperty.setHelpText(
                "Enter the certification Api key header name passed to Audit middleware request.");
        apikeyHeaderProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(apikeyHeaderProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty apikeyProperty = new ProviderConfigProperty();
        apikeyProperty.setName(GsisAuthorizationsProviderMapper.CONF_APIKEY_ATTRIBUTE);
        apikeyProperty.setLabel("Audit middleware certification Api key");
        apikeyProperty
                .setHelpText("Enter the certification api key passed to Audit middleware request.");
        apikeyProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(apikeyProperty);

        // define the property for the roles mapping
        ProviderConfigProperty rolesMappingProperty = new ProviderConfigProperty();
        rolesMappingProperty.setName(GsisAuthorizationsProviderMapper.CONF_ROLES_MAPPING);
        rolesMappingProperty.setLabel("Roles Mapping");
        rolesMappingProperty.setHelpText(
                "Enter the mapping of GSIS role ids to specific application roles in the format 'GsisRoleId=ApplicationRole [Enter] GSISRoleId2=ApplicationRole2'. Split each key-value pair by a new line. Exposed to JWT token as 'mappedRoles' attribute.");
        rolesMappingProperty.setType(ProviderConfigProperty.TEXT_TYPE);
        configProperties.add(rolesMappingProperty);

        return configProperties;
    }
}