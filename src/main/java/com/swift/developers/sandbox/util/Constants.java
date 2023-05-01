package com.swift.developers.sandbox.util;

public final class Constants {
    public static final String GATEWAY_HOST = "host";
    public static final String CALL_TIME_OUT = "call_timeout_sec";
    public static final String X_SWIFT_SIGNATURE_HEADER = "xSwift_Signature_Header";
    public static final String CONNECTOR_HOST = "host";
    public static final String OAUTH_SERVICE = "url";
    public static final String TRACKER_SERVICE_V2 = "tracker_service_v2";
    public static final String TRACKER_SERVICE_V3 = "tracker_service_v3";
    public static final String TRACKER_SERVICE_V4 = "tracker_service_v4";
    public static final String TRACKER_SERVICE_V5 = "tracker_service_v5";
    public static final String TRACKER_SERVICE_V5_STATUS_CONFIRM_CCT = "tracker_service_v5_status_confirm_cct";
    public static final String TRACKER_SERVICE_V5_STATUS_CONFIRM_COV = "tracker_service_v5_status_confirm_cov";
    public static final String TRACKER_SERVICE_V5_STATUS_CONFIRM_FIT = "tracker_service_v5_status_confirm_fit";
    public static final String TRACKER_SERVICE_V5_STATUS_CONFIRM_INST = "tracker_service_v5_status_confirm_inst";
    public static final String TRACKER_SERVICE_V5_STATUS_CONFIRM_UNIVERSAL = "tracker_service_v5_status_confirm_universal";
    public static final String GATEWAY_G4C_SERVICE = "g4c_service";
    public static final String GATEWAY_G4C_SERVICE_V5 = "g4c_service_v5";
    public static final String GATEWAY_GCASE_SERVICE = "gcase_service";
    public static final String GATEWAY_GCASE_SERVICE_V5 = "gcase_service_v5";
    public static final String GATEWAY_TSS_SERVICE = "tss_service";
    public static final String GATEWAY_PREVAL_SERVICE = "preval_service";
    public static final String GATEWAY_PREVAL_SERVICE_V2 = "preval_service_v2";
    public static final String SWIFTREF_SERVICE = "swiftref_service";
    public static final String SWIFTREF_SERVICE_SIPN = "swiftref_service_sipn";
    public static final String MGW_SERVICE = "mgw_service";
    public static final String SADS_SERVICE = "sads_service";
    public static final String KYC_SERVICE = "kyc_service";
    public static final String COMPLIANCE_ANALYTICS_SERVICE = "compliance_analytics_service";
    public static final String BANKING_ANALYTICS_SERVICE = "banking_analytics_service";
    public static final String NOTIF_SUBSCRIBER_SERVICE_V1 = "notif_subscriber_service_v1";
    public static final String NOTIF_PROVIDER_SERVICE_V1 = "notif_provider_service_v1";
    public static final String SLVP_SERVICE_V4 = "slvp_service_v4";
    public static final String SLVP_SERVICE_V5 = "slvp_service_v5";
    public static final String MONITORING_SERVICE_V20 = "monitoring_service_v20";
    public static final String MANAGEMENT_SERVICE_V20 = "mgw-management_service_v20";
    public static final String CONFIGURATION_SERVICE_V20 = "configuration_service_v20";
    public static final String MONITORING_SERVICE_V10 = "monitoring_service_v10";
    public static final String MANAGEMENT_SERVICE_V10 = "mgw-management_service_v10";
    public static final String CONFIGURATION_SERVICE_V10 = "configuration_service_v10";

    public static final String AUDIENCE = "audience";
    public static final String SCOPE = "scope";
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String CONSUMER_SECRET = "consumer_secret";
    public static final String CERT_PATH = "cert_path";
    public static final String CERT_ALIAS = "cert_alias";
    public static final String GATEWAY_TRUST_ALIAS = "trust_alias";
    public static final String CLIENT_ALIAS = "client_alias";
    public static final String GPI_CONNECTOR_TRUST_ALIAS = "trust_alias";
    public static final String CERT_PASSWORD = "cert_password";

    public static final String SNL_HOSTNAME = "hostname";
    public static final String SNL_PORT = "port";
    public static final String SNL_SSL_DN = "ssl_dn";
    public static final String SNL_MP = "mp";
    public static final String SNL_USER_DN = "user_dn";
    public static final String SNL_LAU_KEY = "lau_key";
    public static final String SNL_SSL_PATH = "ssl_path";
    public static final String SNL_SSL_PASSWORD = "ssl_password";
    public static final String SNL_SSL_ALIAS = "ssl_alias";

    public static final String LAU_APPLICATION_ID = "lau_application_id";
    public static final String LAU_VERSION = "lau_version";
    public static final String LAU_KEY = "lau_key";
    public static final String LAU_APPL_API_KEY = "lau_appl_api_key";
    public static final String LAU_RBAC_ROLE = "lau_rbac_role";

    public static final String MANAGEMENT_APIKEY = "management_apikey";
    public static final String CONFIGURATION_APIKEY = "configuration_apikey";
    public static final String MONITORING_APIKEY = "monitoring_apikey";

    public static final String AMQP_CONNECTION_URL = "connection_url";
    public static final String AMQP_OUTBOUND_JMS_QUEUE = "outbound_jms_queue";
    public static final String AMQP_INBOUND_JMS_QUEUE = "inbound_jms_queue";
    public static final String ISO20022_VALIDATE = "iso20022_validate";
    public static final String AMQP_RESPONSE_TIMEOUT = "response_timeout_millisec";

    public static final String IMDG_HAZELCAST_INSTANCE_ADDRESS = "hazelcast_instance_address";
    public static final String IMDG_HAZELCAST_REQUEST_QUEUE = "hazelcast_request_queue";
    public static final String IMDG_HAZELCAST_RESPONSE_JMS_QUEUE = "hazelcast_response_queue";
    public static final String IMDG_HAZELCAST_ISO20022_VALIDATE = "hazelcast_iso20022_validate";
    public static final String IMDG_HAZELCAST_RESPONSE_TIMEOUT = "hazelcast_response_timeout_millisec";

    public static final String ALGORITHM = "AES";
    public static final String CIPHER = "AES/GCM/NoPadding";

    public static final String BASIC_USERNAME = "username";
    public static final String BASIC_PASSWORD = "password";

    public static final String YAML_CONFIGURATION = "configuration";
    public static final String YAML_CONFIGURATION_HOSTS = "hosts";
    public static final String YAML_CONFIGURATION_API_GATEWAY = "api_gateway";
    public static final String YAML_CONFIGURATION_AUTHORIZATION_SERVICE = "authorization_service";
    public static final String YAML_CONFIGURATION_AUTHORIZATION_SERVICE_V2 = "authorization_service_v2";
    public static final String YAML_CONFIGURATION_GPI_CONNECTOR = "gpi_connector";
    public static final String YAML_CONFIGURATION_MGW = "microgateway";
    public static final String YAML_CONFIGURATION_API = "api";
    public static final String YAML_CONFIGURATION_AMQP = "amqp";
    public static final String YAML_CONFIGURATION_IMDG = "imdg";
    public static final String YAML_CONFIGURATION_SERVICES = "services";
    public static final String YAML_CONFIGURATION_SECURITY_FOOTPRINT = "security_footprints";
    public static final String YAML_CONFIGURATION_COMMON = "common";
    public static final String YAML_CONFIGURATION_BASIC = "basic";
    public static final String YAML_CONFIGURATION_SOFTWARE_CERTIFICATE = "software_certificate";
    public static final String MGW_JWS_AUTH = "mgwjws";
    public static final String MGW_APPLICATION_NAME = "application_name";
    public static final String MGW_PROFILE_ID = "profile_id";
    public static final String MGW_SHARED_SECRET = "shared_secret";
    public static final String ENABLE_ABSPATH_VALIDATION = "ENABLE_ABSPATH_VALIDATION";
    public static final String ENABLE_OKHTTP_SESSION = "ENABLE_OKHTTP_SESSION";

}
