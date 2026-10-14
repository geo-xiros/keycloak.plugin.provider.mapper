package gr.microbase.gsis.identity.keycloak.mapper;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.provider.ProviderConfigProperty;

import gr.microbase.gsis.identity.keycloak.mapper.Common.Models.AuditRecordHelper;

public class GsisUserIdProviderMapperConfig {
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
        attributeNameProperty.setName(GsisUserIdProviderMapper.CONF_USER_ATTRIBUTE);
        attributeNameProperty.setLabel("User Attribute Name");
        attributeNameProperty.setHelpText("User attribute name to store information into.");
        attributeNameProperty.setType(ProviderConfigProperty.USER_PROFILE_ATTRIBUTE_LIST_TYPE);
        configProperties.add(attributeNameProperty);

        // Define the property for the converter url
        ProviderConfigProperty converterUrlProperty = new ProviderConfigProperty();
        converterUrlProperty.setName(GsisUserIdProviderMapper.CONF_TAX_CONVERTER_URL);
        converterUrlProperty.setLabel("Tax id to username converter API");
        converterUrlProperty.setHelpText("Enter the external API Url used to convert Tax Id to User Id.");
        converterUrlProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(converterUrlProperty);

        // Define the property for the username
        ProviderConfigProperty usernameProperty = new ProviderConfigProperty();
        usernameProperty.setName(GsisUserIdProviderMapper.CONF_USERNAME);
        usernameProperty.setLabel("API username");
        usernameProperty.setHelpText("Enter external API username.");
        usernameProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(usernameProperty);

        // Define the property for the password
        ProviderConfigProperty passwordProperty = new ProviderConfigProperty();
        passwordProperty.setName(GsisUserIdProviderMapper.CONF_PASSWORD);
        passwordProperty.setLabel("API password");
        passwordProperty.setHelpText("Enter external API password.");
        passwordProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(passwordProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty certificationPathProperty = new ProviderConfigProperty();
        certificationPathProperty.setName(GsisUserIdProviderMapper.CONF_CERT_PATH_ATTRIBUTE);
        certificationPathProperty.setLabel("Audit middleware certification path");
        certificationPathProperty
                .setHelpText("Enter the certification path used for Audit middleware request.");
        certificationPathProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(certificationPathProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty certificationPasswordProperty = new ProviderConfigProperty();
        certificationPasswordProperty.setName(GsisUserIdProviderMapper.CONF_CERT_PASSWORD_ATTRIBUTE);
        certificationPasswordProperty.setLabel("Audit middleware certification password");
        certificationPasswordProperty
                .setHelpText("Enter the certification password used for Audit middleware request.");
        certificationPasswordProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(certificationPasswordProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty apikeyHeaderProperty = new ProviderConfigProperty();
        apikeyHeaderProperty.setName(GsisUserIdProviderMapper.CONF_APIKEYHEADER_ATTRIBUTE);
        apikeyHeaderProperty.setLabel("Audit middleware certification Api key header name");
        apikeyHeaderProperty.setHelpText(
                "Enter the certification Api key header name passed to Audit middleware request.");
        apikeyHeaderProperty.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(apikeyHeaderProperty);

        // Define the property for the audit protocol
        ProviderConfigProperty apikeyProperty = new ProviderConfigProperty();
        apikeyProperty.setName(GsisUserIdProviderMapper.CONF_APIKEY_ATTRIBUTE);
        apikeyProperty.setLabel("Audit middleware certification Api key");
        apikeyProperty
                .setHelpText("Enter the certification api key passed to Audit middleware request.");
        apikeyProperty.setType(ProviderConfigProperty.PASSWORD);
        configProperties.add(apikeyProperty);

        ProviderConfigProperty updateUsernameWithUserIdProperty = new ProviderConfigProperty();
        updateUsernameWithUserIdProperty.setName(GsisUserIdProviderMapper.UPDATE_USERNAME_WITH_USER_ID);
        updateUsernameWithUserIdProperty.setLabel("Update Username with GSIS Username");
        updateUsernameWithUserIdProperty.setHelpText(
                "Update Username with GSIS Username retrieved from Tax Id to Username external converter.");
        updateUsernameWithUserIdProperty.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        updateUsernameWithUserIdProperty.setDefaultValue(true);
        configProperties.add(updateUsernameWithUserIdProperty);

        return configProperties;
    }
}