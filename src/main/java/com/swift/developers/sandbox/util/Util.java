package com.swift.developers.sandbox.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.*;
import com.swift.developers.sandbox.exception.ApiSessionException;
import com.swift.sdk.common.entity.*;
import com.swift.sdk.common.handler.config.SDKConfigLoader;
import com.swift.sdk.common.http.OKHttpClientSDKImpl;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

public class Util {

    private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    ;
    private static final String PEM_CERTIFICATE_PREFIX = "-----BEGIN CERTIFICATE-----";
    private static final String PEM_CERTIFICATE_POSTFIX = "-----END CERTIFICATE-----";

    public static boolean isNotNullOrEmpty(String string) {
        return ((string != null) && (string.length() != 0));
    }

    public static void validateProxy(ProxyParameters[] proxyParameters) throws ApiSessionException {
        if (proxyParameters != null) {
            ProxyParameters[] uniqueProxyParameters = Arrays.stream(proxyParameters).distinct()
                    .toArray(length -> (ProxyParameters[]) Array.newInstance(ProxyParameters.class, length));
            if (uniqueProxyParameters.length != proxyParameters.length) {
                throw new ApiSessionException(
                        "Proxy should be unique by combination of host and port. The list of proxies contains duplicates:"
                                + System.lineSeparator() + Arrays.toString(proxyParameters));
            }
        }
    }

    public static Certificate extractCertificate(String alias, KeyStore keyStore) throws ApiSessionException {
        if ((alias == null) || alias.isEmpty()) {
            return null;
        }
        if (keyStore == null) {
            return null;
        }
        Certificate cert = null;
        try {
            cert = keyStore.getCertificate(alias);
        } catch (KeyStoreException ex) {
            LOG.error("Failed to extract certificate by alias = {}.{}{}", alias, System.lineSeparator(), ex.getMessage());
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return cert;
    }

    public static String extractPemCertificate(Certificate certificate) throws ApiSessionException {
        if (certificate == null) {
            return null;
        }

        String certificateBase64;

        try {
            certificateBase64 = Base64.getEncoder().encodeToString(certificate.getEncoded());
        } catch (CertificateEncodingException ex) {
            LOG.error("Failed to encode certificate.{}{}", System.lineSeparator(), ex.getMessage());
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return PEM_CERTIFICATE_PREFIX + System.lineSeparator() + certificateBase64
                + System.lineSeparator() + PEM_CERTIFICATE_POSTFIX;
    }

    public static ConnectionInfo createConnectionInfo(JsonObject configJsonObject) {
        ConnectionInfo connInfo = new ConnectionInfo();

        JsonObject configurationObject = returnConfigObject(configJsonObject, Constants.YAML_CONFIGURATION);
        if (configurationObject != null) {
            JsonObject hostsObject = returnConfigObject(configurationObject, Constants.YAML_CONFIGURATION_HOSTS);
            if (hostsObject != null) {

                //set the call_timeout_sec propriety
                if (hostsObject.get(Constants.CALL_TIME_OUT) != null) {
                    connInfo.setCallTimeoutSec(returnConfigValue(hostsObject, Constants.CALL_TIME_OUT));
                }

                //set the xSwift_Signature_Header
                if (hostsObject.get(Constants.X_SWIFT_SIGNATURE_HEADER) != null) {
                    connInfo.setxSwift_Signature_Header(returnConfigValue(hostsObject, Constants.X_SWIFT_SIGNATURE_HEADER));
                }

                JsonObject apiGatewayObject = returnConfigObject(hostsObject, Constants.YAML_CONFIGURATION_API_GATEWAY);
                if (apiGatewayObject != null) {
                    connInfo.setGatewayHost(returnConfigValue(apiGatewayObject, Constants.GATEWAY_HOST));
                    connInfo.setTrustAliasGateway(returnConfigValue(apiGatewayObject, Constants.GATEWAY_TRUST_ALIAS));
                    connInfo.setClientID(returnConfigValue(apiGatewayObject, Constants.CONSUMER_KEY));
                    connInfo.setClientSecret(returnConfigValue(apiGatewayObject, Constants.CONSUMER_SECRET));

                    JsonObject authorizationServiceObject = returnConfigObject(apiGatewayObject, Constants.YAML_CONFIGURATION_AUTHORIZATION_SERVICE);
                    if (authorizationServiceObject != null) {
                        connInfo.setService(returnConfigValue(authorizationServiceObject, Constants.OAUTH_SERVICE));
                        connInfo.setAudience(returnConfigValue(authorizationServiceObject, Constants.AUDIENCE));
                        connInfo.setScope(returnConfigValue(authorizationServiceObject, Constants.SCOPE));
                    }

                    JsonObject authorizationServiceClientCredObject = returnConfigObject(apiGatewayObject, Constants.YAML_CONFIGURATION_AUTHORIZATION_SERVICE_V2);
                    if (null != authorizationServiceClientCredObject) {
                        connInfo.setServiceV2(returnConfigValue(authorizationServiceClientCredObject, Constants.OAUTH_SERVICE));
                        connInfo.setAudienceV2(returnConfigValue(authorizationServiceClientCredObject, Constants.AUDIENCE));
                        connInfo.setScopeV2(returnConfigValue(authorizationServiceClientCredObject, Constants.SCOPE));
                    }
                }

                JsonObject gpiConnectorObject = returnConfigObject(hostsObject, Constants.YAML_CONFIGURATION_GPI_CONNECTOR);
                if (gpiConnectorObject != null) {
                    connInfo.setTrustAliasConnector(returnConfigValue(gpiConnectorObject, Constants.GPI_CONNECTOR_TRUST_ALIAS));
                    connInfo.setConnectorHost(returnConfigValue(gpiConnectorObject, Constants.CONNECTOR_HOST));
                }

                JsonObject microgatewayObject = returnConfigObject(hostsObject, Constants.YAML_CONFIGURATION_MGW);

                if (microgatewayObject != null) {
                    connInfo.setMicrogatewayHost(returnConfigValue(microgatewayObject, Constants.GATEWAY_HOST));
                    connInfo.setMgwService(returnConfigValue(microgatewayObject, Constants.MGW_SERVICE));
                    connInfo.setTrustAliasMgw(returnConfigValue(microgatewayObject, Constants.GATEWAY_TRUST_ALIAS));
                    if (microgatewayObject.get(Constants.CLIENT_ALIAS) != null) {
                        connInfo.setClientAliasMgw(returnConfigValue(microgatewayObject, Constants.CLIENT_ALIAS));
                    }


                    JsonObject apiObject = returnConfigObject(microgatewayObject, Constants.YAML_CONFIGURATION_API);
                    if (null != apiObject) {

                        ApiMicroservicesConnectionInfo apiMicroservicesConnectionInfo =
                                ApiMicroservicesConnectionInfo.builder().managementApiKey(returnConfigValue(apiObject, Constants.MANAGEMENT_APIKEY))
                                        .configurationApiKey(returnConfigValue(apiObject, Constants.CONFIGURATION_APIKEY))
                                        .monitoringApiKey(returnConfigValue(apiObject, Constants.MONITORING_APIKEY))
                                        .build();
                        connInfo.setApiMicroservicesConnectionInfo(apiMicroservicesConnectionInfo);
                    }


                    JsonObject amqpObject = returnConfigObject(microgatewayObject, Constants.YAML_CONFIGURATION_AMQP);
                    if (null != amqpObject) {
                        String iso20022Validate = returnConfigValue(amqpObject, Constants.ISO20022_VALIDATE);
                        Iso20022ValidationType validationType =
                                Iso20022ValidationType.valueOf(iso20022Validate);

                        AmqpConnectionInfo amqpConnectionInfo =
                                AmqpConnectionInfo.builder().amqpConnectionUrl(returnConfigValue(amqpObject, Constants.AMQP_CONNECTION_URL))
                                        .amqpInboundRequestQueue(returnConfigValue(amqpObject, Constants.AMQP_INBOUND_JMS_QUEUE))
                                        .amqpOutboundRequestQueue(returnConfigValue(amqpObject, Constants.AMQP_OUTBOUND_JMS_QUEUE))
                                        .iso20022ValidationType(validationType)
                                        .responseTimeoutMillisec(Long.parseLong(returnConfigValue(amqpObject, Constants.AMQP_RESPONSE_TIMEOUT)))
                                        .build();
                        connInfo.setAmqpConnectionInfo(amqpConnectionInfo);
                    }

                    JsonObject imdgObject = returnConfigObject(microgatewayObject, Constants.YAML_CONFIGURATION_IMDG);
                    if (null != imdgObject) {
                        String iso20022Validate = returnConfigValue(imdgObject, Constants.IMDG_HAZELCAST_ISO20022_VALIDATE);
                        Iso20022ValidationType validationType =
                                Iso20022ValidationType.valueOf(iso20022Validate);

                        HazelcastConnectionInfo hazelcastConnectionInfo =
                                HazelcastConnectionInfo.builder().hazelcastInstanceAddress(returnConfigValue(imdgObject, Constants.IMDG_HAZELCAST_INSTANCE_ADDRESS))
                                        .hazelcastRequestQueue(returnConfigValue(imdgObject, Constants.IMDG_HAZELCAST_REQUEST_QUEUE))
                                        .hazelcastResponseQueue(returnConfigValue(imdgObject, Constants.IMDG_HAZELCAST_RESPONSE_JMS_QUEUE))
                                        .iso20022ValidationType(validationType)
                                        .hazelcastResponseTimeoutMillisec(Long.parseLong(returnConfigValue(imdgObject, Constants.IMDG_HAZELCAST_RESPONSE_TIMEOUT)))
                                        .build();
                        connInfo.setHazelcastConnectionInfo(hazelcastConnectionInfo);
                    }
                }

                List<com.swift.sdk.common.entity.ProxyParameters> proxyParametersList = new ArrayList<>();
                JsonArray forwardProxyArray = returnConfigArray(hostsObject, "forward_proxies");

                if (forwardProxyArray != null) {
                    for (JsonElement forwardProxyElement : forwardProxyArray) {
                        JsonObject proxyObj = forwardProxyElement.getAsJsonObject();

                        if (proxyObj != null) {
                            com.swift.sdk.common.entity.ProxyParameters proxyParameters = new com.swift.sdk.common.entity.ProxyParameters();
                            proxyParameters.setHost(returnConfigValue(proxyObj, Constants.SNL_HOSTNAME));
                            proxyParameters.setPort(returnConfigValue(proxyObj, Constants.SNL_PORT));
                            proxyParameters.setUser(returnConfigValue(proxyObj, Constants.BASIC_USERNAME, false));
                            proxyParameters.setPassword(returnConfigValue(proxyObj, Constants.BASIC_PASSWORD, false));
                            proxyParametersList.add(proxyParameters);
                        }
                    }
                    connInfo.setProxyList(proxyParametersList);
                }
            }

            JsonObject securityFootprintsObject = returnConfigObject(configurationObject,
                    Constants.YAML_CONFIGURATION_SECURITY_FOOTPRINT);
            if (securityFootprintsObject != null) {
                JsonObject commonObject = returnConfigObject(securityFootprintsObject,
                        Constants.YAML_CONFIGURATION_COMMON);
                if (commonObject != null) {
                    connInfo.setCertPath(returnConfigValue(commonObject, Constants.CERT_PATH));
                    connInfo.setCertPassword(returnConfigValue(commonObject, Constants.CERT_PASSWORD));
                }

                JsonObject softwareCertificateObject = returnConfigObject(securityFootprintsObject,
                        Constants.YAML_CONFIGURATION_SOFTWARE_CERTIFICATE);
                if (softwareCertificateObject != null) {
                    connInfo.setCertAlias(returnConfigValue(softwareCertificateObject, Constants.CERT_ALIAS));
                }

                List<com.swift.sdk.common.entity.SNLConnectionInfo> sagConnectionInfo = new ArrayList<>();
                JsonArray sagArray = returnConfigArray(securityFootprintsObject, "sags");
                if (sagArray != null) {
                    for (JsonElement sagElement : sagArray) {
                        JsonObject sagObj = sagElement.getAsJsonObject();
                        if ((sagObj != null) && (commonObject != null)) {
                            com.swift.sdk.common.entity.SNLConnectionInfo snlConnectionInfo = new com.swift.sdk.common.entity.SNLConnectionInfo();
                            snlConnectionInfo.setHostname(returnConfigValue(sagObj, Constants.SNL_HOSTNAME));
                            snlConnectionInfo.setPort(returnConfigValue(sagObj, Constants.SNL_PORT));
                            snlConnectionInfo.setSslDN(returnConfigValue(sagObj, Constants.SNL_SSL_DN));
                            snlConnectionInfo.setUserDN(returnConfigValue(sagObj, Constants.SNL_USER_DN));
                            snlConnectionInfo.setMessagePartner(returnConfigValue(sagObj, Constants.SNL_MP));
                            snlConnectionInfo.setLauKey(returnConfigValue(sagObj, Constants.SNL_LAU_KEY));
                            snlConnectionInfo.setTrustStoragePath(returnConfigValue(commonObject, Constants.CERT_PATH));
                            snlConnectionInfo
                                    .setTrustStoragePass(returnConfigValue(commonObject, Constants.CERT_PASSWORD));
                            snlConnectionInfo.setTrustStorageAlias(returnConfigValue(sagObj, Constants.SNL_SSL_ALIAS));
                            sagConnectionInfo.add(snlConnectionInfo);
                        }
                    }
                    connInfo.setSnlConnectionInfo(sagConnectionInfo);
                }

                JsonArray lausArray = returnConfigArray(securityFootprintsObject, "laus");
                if (lausArray != null) {
                    List<com.swift.sdk.common.entity.LAUInfo> lauInfoList = new ArrayList<>();
                    for (JsonElement lauElement : lausArray) {
                        JsonObject lauObj = lauElement.getAsJsonObject();
                        if (lauObj != null) {
                            com.swift.sdk.common.entity.LAUInfo lauInfo = new com.swift.sdk.common.entity.LAUInfo();

                            lauInfo.setLauApplicationID(returnConfigValue(lauObj, Constants.LAU_APPLICATION_ID));
                            lauInfo.setLauVersion(returnConfigValue(lauObj, Constants.LAU_VERSION));
                            lauInfo.setLauKey(returnConfigValue(lauObj, Constants.LAU_KEY));
                            String lauApplApiKey = returnConfigValue(lauObj, Constants.LAU_APPL_API_KEY);
                            String lauRbacRole = returnConfigValue(lauObj, Constants.LAU_RBAC_ROLE);
                            lauInfo.setLauSigned("(ApplAPIKey=" + lauApplApiKey + "),(RBACRole=[" + lauRbacRole + "])");

                            lauInfoList.add(lauInfo);
                        }
                    }
                    connInfo.setLauInfo(lauInfoList);
                }

                List<com.swift.sdk.common.entity.MGWConnectionInfo> mgwConnectionInfoList = new ArrayList<>();
                JsonArray mgwArray = returnConfigArray(securityFootprintsObject, "mgwjws");

                if (mgwArray != null) {

                    for (JsonElement mgwElement : mgwArray) {
                        JsonObject mgwObj = mgwElement.getAsJsonObject();

                        if ((mgwObj != null) && (commonObject != null)) {
                            com.swift.sdk.common.entity.MGWConnectionInfo mgwConnectionInfo = new com.swift.sdk.common.entity.MGWConnectionInfo();
                            mgwConnectionInfo
                                    .setMgwApplicationName(returnConfigValue(mgwObj, Constants.MGW_APPLICATION_NAME));

                            mgwConnectionInfo.setMgwProfileId(returnConfigValue(mgwObj, Constants.MGW_PROFILE_ID));
                            mgwConnectionInfo
                                    .setMgwSharedSecret(returnConfigValue(mgwObj, Constants.MGW_SHARED_SECRET));
                            mgwConnectionInfoList.add(mgwConnectionInfo);
                        }
                    }
                    connInfo.setMgwConnectionInfo(mgwConnectionInfoList);
                }

                JsonObject basicObject = returnConfigObject(securityFootprintsObject,
                        Constants.YAML_CONFIGURATION_BASIC);
                if (basicObject != null) {
                    connInfo.setUsername(returnConfigValue(basicObject, Constants.BASIC_USERNAME));
                    connInfo.setPassword(returnConfigValue(basicObject, Constants.BASIC_PASSWORD));
                }
            }
        }

        //Important step. Do not remove !
        connInfo.setHttpBuilderSDK(OKHttpClientSDKImpl.HttpBuilder.class); //default HttpImplementation needs to be OKHttp for SWIFT SDK
        ConfigInfo configInfo = new ConfigInfo(connInfo.getCallTimeoutSec(), connInfo.getCertAlias(),
                connInfo.getTrustAliasGateway(), connInfo.getTrustAliasConnector(), connInfo.getTrustAliasMgw(), connInfo.getClientAliasMgw(),
                connInfo.getCertPassword(), connInfo.getClientID(), connInfo.getClientSecret(), connInfo.getCertPath(),
                connInfo.getGatewayHost(), connInfo.getMicrogatewayHost(), connInfo.getConnectorHost(), connInfo.getService(),
                connInfo.getScope(), connInfo.getAudience(), connInfo.getCaCert(), connInfo.getUsername(), connInfo.getPassword(),
                connInfo.getMgwService(), connInfo.getApiMicroservicesConnectionInfo(), connInfo.getServiceV2(), connInfo.getScopeV2(),
                connInfo.getAudienceV2(), connInfo.getProxyList(), connInfo.getSnlConnectionInfo(), connInfo.getMgwConnectionInfo(),
                connInfo.getLauInfo(), connInfo.getxSwift_Signature_Header(), null, true, connInfo.getHttpBuilderSDK());
        if (!SDKConfigLoader.load(configInfo).equals(configInfo)) {
            LOG.error("Failed to init the Generic Config! Likely a different Config was already init by another task (mismatch).");
            throw new IllegalStateException("Failed to init the Generic Config! Likely different Config was already init by another task (mismatch).");
        }

        return connInfo;
    }

    public static JsonObject returnConfigObject(JsonObject parentJsonObject, String parentProperty) {
        JsonObject propertyParentObject = null;
        if (parentJsonObject.has(parentProperty)) {
            propertyParentObject = parentJsonObject.getAsJsonObject(parentProperty);
        }
        return propertyParentObject;
    }

    public static String returnConfigValue(JsonObject parentJsonObject, String property) {
        String value;

        JsonElement valueJson = parentJsonObject.get(property);
        if (valueJson != null) {
            value = valueJson.getAsString().isEmpty() ? null : valueJson.getAsString();
        } else {
            throw new InvalidParameterException(property + " property expected and is not provided in config file.");
        }

        return value;
    }

    public static String returnConfigValue(JsonObject parentJsonObject, String property,
                                           boolean required) {
        String value;

        JsonElement valueJson = parentJsonObject.get(property);
        if (valueJson != null) {
            value = valueJson.getAsString().isEmpty() ? null : valueJson.getAsString();
        } else {
            if (required) {
                throw new InvalidParameterException(property +
                        " property expected and is not provided in config file.");
            } else {
                value = null;
            }
        }

        return value;
    }

    public static String returnConfigValue(JsonObject jsonObject, String property, List<String> parentProperties) {
        JsonObject propertyParentObject = jsonObject;
        if (parentProperties != null) {
            for (String parentProperty : parentProperties) {
                if (propertyParentObject.has(parentProperty)) {
                    propertyParentObject = propertyParentObject.getAsJsonObject(parentProperty);
                }
            }
        }
        return returnConfigValue(propertyParentObject, property);
    }

    public static JsonArray returnConfigArray(JsonObject jsonObject, String property) {
        JsonArray propertyParentArray = null;
        if (jsonObject.has(property)) {
            propertyParentArray = jsonObject.getAsJsonArray(property);
        }
        return propertyParentArray;
    }

    public static String returnConnectionConfigValue(JsonObject jsonObject, String property) {
        return returnConfigValue(jsonObject, property, List.of("connection"));
    }

    public static String returnServicePropertyValue(JsonObject jsonObject, String property) {
        return returnConfigValue(jsonObject, property, Arrays.asList("properties", "service"));
    }

    public static String returnNotificationPropertyValue(JsonObject jsonObject, String property) {
        return returnConfigValue(jsonObject, property, Arrays.asList("properties", "notification"));
    }

    public static String returnSubscriptionPropertyValue(JsonObject jsonObject, String property) {
        return returnConfigValue(jsonObject, property, Arrays.asList("properties", "subscription"));
    }

    public static JsonObject readConfigurationPropertiesJson(String configPath) throws ApiSessionException {
        JsonObject jsonObject;

        try {
            jsonObject = JsonParser.parseReader(new FileReader(configPath))
                    .getAsJsonObject();
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException ex) {
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return jsonObject;
    }

    public static JsonObject readConfigurationPropertiesYaml(String configPath) throws ApiSessionException {
        byte[] fileContent;
        JsonObject jsonObject;

        try {
            fileContent = Files.readAllBytes((new File(configPath)).toPath());
            String tmpStr = new String(fileContent, StandardCharsets.UTF_8);
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(tmpStr, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();

            jsonObject = JsonParser.parseReader(new StringReader(jsonWriter.writeValueAsString(obj)))
                    .getAsJsonObject();

        } catch (IOException | JsonIOException | JsonSyntaxException ex) {
            throw new ApiSessionException(ex.getMessage(), ex);
        }

        return jsonObject;
    }

    public static String getBasePath(JsonObject configJson, String host, String service) {
        JsonObject configurationObject = returnConfigObject(configJson, Constants.YAML_CONFIGURATION);
        JsonObject hostsObject = returnConfigObject(configurationObject, Constants.YAML_CONFIGURATION_HOSTS);
        JsonObject apiGatewayObject = returnConfigObject(hostsObject, Constants.YAML_CONFIGURATION_API_GATEWAY);
        JsonObject servicesObject = returnConfigObject(configurationObject, Constants.YAML_CONFIGURATION_SERVICES);

        if (!host.isEmpty()) {
            return returnConfigValue(apiGatewayObject, host) + returnConfigValue(servicesObject, service);
        } else {
            return returnConfigValue(servicesObject, service);
        }
    }
    
    /*
    public static String getBasePath(JsonObject configJson, String host, String service) {
        return returnConnectionConfigValue(configJson, host) + returnConnectionConfigValue(configJson, service);
    }
    */

    public static String getStackTrace(Throwable ex) {
        return ExceptionUtils.getStackTrace(ex);
    }

    /**
     * Base64 encode and zip the string value.
     *
     * @param value
     * @return
     * @throws ApiSessionException
     */
    public static String zipBase64Encode(String value) throws ApiSessionException {
        Base64.Encoder encoder = Base64.getUrlEncoder();
        try {
            if ((value == null) || value.isEmpty()) {
                throw new ApiSessionException("Cannot zip null or empty string.");
            }
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(Deflater.DEFLATED, true);
            DeflaterOutputStream deflaterStream = new DeflaterOutputStream(bytesOut, deflater);
            deflaterStream.write(value.getBytes(StandardCharsets.UTF_8));
            deflaterStream.finish();

            return encoder.encodeToString(bytesOut.toByteArray());
        } catch (IOException ex) {
            throw new ApiSessionException("Unable to Deflate and Base64 encode", ex);
        }
    }

    /**
     * Unzip and Base64 decode the string value.
     *
     * @param value
     * @return
     * @throws ApiSessionException
     */
    public static String baseDecodeAndUnzip(String value) throws ApiSessionException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        try {
            if ((value == null) || value.isEmpty()) {
                throw new ApiSessionException("Cannot unzip null or empty string.");
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Inflater decompresser = new Inflater(true);
            InflaterOutputStream inflaterOutputStream = new InflaterOutputStream(bos, decompresser);
            inflaterOutputStream.write(decoder.decode(value));
            inflaterOutputStream.close();

            return bos.toString(StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new ApiSessionException("Unable to unzip and Base64 decode", ex);
        }
    }

    public enum CertType {SOFT, HARD}
}
