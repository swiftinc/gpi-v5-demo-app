package com.swift.developers.sandbox.session;

import com.google.gson.JsonObject;
import com.hazelcast.internal.util.CollectionUtil;
import com.swift.commons.oauth.token.OAuthTokenHolder;
import com.swift.developers.sandbox.exception.ApiSessionException;
import com.swift.developers.sandbox.util.SwiftSdkUtil;
import com.swift.sdk.common.entity.ConnectionInfo;
import com.swift.sdk.common.entity.ProxyParameters;
import com.swift.sdk.session.impl.SessionImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
@Getter
@Setter
public class SandboxApiSession extends SessionImpl {

    private final String configFile;
    private final ConnectionInfo connection;
    private final OAuthTokenHolder tokenHolder;
    private final SwiftSdkUtil.CertType type;

    private JsonObject configJson = null;
    private ProxyParameters[] proxy = null;

    public SandboxApiSession(String configFile, SwiftSdkUtil.CertType type) throws ApiSessionException {
        this(configFile, type, null);
    }

    public SandboxApiSession(String configFile, SwiftSdkUtil.CertType type, String scope) throws ApiSessionException {
        this.configFile = configFile;
        /* Certificate type - HSM or Channel. */
        this.type = type;

        try {
            log.info("\n#################   Initializing Basic Session Object   ######################\n");
            log.info("Initializing using the configuration file {}.", configFile);

            /* Supports both YAML & JSON configurations. */
            if (configFile.endsWith(".yaml")) {
                this.configJson = SwiftSdkUtil.readConfigurationPropertiesYaml(configFile);
            } else if (configFile.endsWith(".json")) {
                this.configJson = SwiftSdkUtil.readConfigurationPropertiesJson(configFile);
            }

            this.connection = SwiftSdkUtil.createConnectionInfo(configJson);
            if (scope != null) {
                this.connection.setScope(scope);
            }

            if (!CollectionUtil.isEmpty(connection.getProxyList())) {
                /* Forward proxies are defined. */
                this.proxy = connection.getProxyList()
                        .toArray(ProxyParameters[]::new);
            }

            if (Objects.equals(this.type, SwiftSdkUtil.CertType.HARD)) {
                this.tokenHolder = super.getAccessToken(this.connection, this.proxy, 0);
            } else {
                this.tokenHolder = super.getAccessTokenChannelCert(this.connection, this.proxy);
            }
        } catch (Exception ex) {
            log.error("Failed to initialize. Error : " + ex.getMessage() +
                    "\nStack Trace : \n" + SwiftSdkUtil.getStackTrace(ex));
            throw (ex instanceof ApiSessionException) ? (ApiSessionException) ex :
                    new ApiSessionException(ex.getMessage(), ex);
        }
    }

    /**
     * Returns the access token.
     */
    public String getAccessToken() {
        return this.tokenHolder.getAccessToken();
    }

    /**
     * Returns the refresh token.
     */
    public String getRefreshToken() {
        return this.tokenHolder.getRefreshToken();
    }

    public String getTokenExpiry() {
        return this.tokenHolder.getTokenExpiry();
    }

    public String getRefreshExpiry() {
        return this.tokenHolder.getRefreshExpiry();
    }
}
