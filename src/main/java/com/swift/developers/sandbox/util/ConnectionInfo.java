package com.swift.developers.sandbox.util;

import com.swift.sdk.common.api.http.interfaces.HttpBuilderSDK;
import com.swift.sdk.common.entity.AmqpConnectionInfo;
import com.swift.sdk.common.entity.ApiMicroservicesConnectionInfo;
import com.swift.sdk.common.entity.HazelcastConnectionInfo;

import java.util.List;

public class ConnectionInfo {

    private String callTimeoutSec;
    private String certAlias;
    private String trustAliasGateway;
    private String trustAliasConnector;
    private String certPassword;
    private String clientID;
    private String clientSecret;
    private String certPath;
    private String gatewayHost;
    private String connectorHost;
    private String service;
    private String scope;
    private String audience;
    private String caCert;
    private String microgatewayHost;
    private String mgwService;
    private String trustAliasMgw;
    private String clientAliasMgw;
    
    private String username;
    private String password;

    private ApiMicroservicesConnectionInfo apiMicroservicesConnectionInfo;
    private AmqpConnectionInfo amqpConnectionInfo;
    private HazelcastConnectionInfo hazelcastConnectionInfo;
    private String serviceV2;
    private String scopeV2;
    private String audienceV2;
    private String xSwift_Signature_Header;
    
    private List<com.swift.sdk.common.entity.ProxyParameters> proxyList;
    private List<com.swift.sdk.common.entity.SNLConnectionInfo> snlConnectionInfo;
    private List<com.swift.sdk.common.entity.MGWConnectionInfo> mgwConnectionInfo;
    private List<com.swift.sdk.common.entity.LAUInfo> lauInfo;

    private Class<? extends HttpBuilderSDK> HttpBuilderSDK;

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    public String getTrustAliasGateway() {
        return trustAliasGateway;
    }

    public void setTrustAliasGateway(String trustAliasGateway) {
        this.trustAliasGateway = trustAliasGateway;
    }

    public String getTrustAliasConnector() {
        return trustAliasConnector;
    }

    public void setTrustAliasConnector(String trustAliasConnector) {
        this.trustAliasConnector = trustAliasConnector;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getGatewayHost() {
        return gatewayHost;
    }

    public void setGatewayHost(String gatewayHost) {
        this.gatewayHost = gatewayHost;
    }

    public String getConnectorHost() {
        return connectorHost;
    }

    public void setConnectorHost(String connectorHost) {
        this.connectorHost = connectorHost;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAudience() {
        return audience;
    }

    public void setAudience(String audience) {
        this.audience = audience;
    }

    public String getCaCert() {
        return caCert;
    }

    public void setCaCert(String caCert) {
        this.caCert = caCert;
    }

    public String getMicrogatewayHost() {
		return microgatewayHost;
	}

	public void setMicrogatewayHost(String microgatewayHost) {
		this.microgatewayHost = microgatewayHost;
	}

	public String getMgwService() {
		return mgwService;
	}

	public void setMgwService(String mgwService) {
		this.mgwService = mgwService;
	}

	public String getTrustAliasMgw() {
		return trustAliasMgw;
	}

	public void setTrustAliasMgw(String trustAliasMgw) {
		this.trustAliasMgw = trustAliasMgw;
	}

	public List<com.swift.sdk.common.entity.SNLConnectionInfo> getSnlConnectionInfo() {
        return snlConnectionInfo;
    }

    public void setSnlConnectionInfo(List<com.swift.sdk.common.entity.SNLConnectionInfo> snlConnectionInfo) {
        this.snlConnectionInfo = snlConnectionInfo;
    }

    public List<com.swift.sdk.common.entity.LAUInfo> getLauInfo() {
        return lauInfo;
    }

    public void setLauInfo(List<com.swift.sdk.common.entity.LAUInfo> lauInfo) {
        this.lauInfo = lauInfo;
    }

    public List<com.swift.sdk.common.entity.MGWConnectionInfo> getMgwConnectionInfo() {
		return mgwConnectionInfo;
	}

	public void setMgwConnectionInfo(List<com.swift.sdk.common.entity.MGWConnectionInfo> mgwConnectionInfo) {
		this.mgwConnectionInfo = mgwConnectionInfo;
	}

	public List<com.swift.sdk.common.entity.ProxyParameters> getProxyList() {
		return proxyList;
	}

	public void setProxyList(List<com.swift.sdk.common.entity.ProxyParameters> proxyList) {
		this.proxyList = proxyList;
	}

	public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ApiMicroservicesConnectionInfo getApiMicroservicesConnectionInfo() {
        return apiMicroservicesConnectionInfo;
    }

    public void setApiMicroservicesConnectionInfo(ApiMicroservicesConnectionInfo apiMicroservicesConnectionInfo) {
        this.apiMicroservicesConnectionInfo = apiMicroservicesConnectionInfo;
    }

    public AmqpConnectionInfo getAmqpConnectionInfo() {
        return amqpConnectionInfo;
    }

    public void setAmqpConnectionInfo(AmqpConnectionInfo amqpConnectionInfo) {
        this.amqpConnectionInfo = amqpConnectionInfo;
    }

    public HazelcastConnectionInfo getHazelcastConnectionInfo() {
        return hazelcastConnectionInfo;
    }

    public void setHazelcastConnectionInfo(HazelcastConnectionInfo hazelcastConnectionInfo) {
        this.hazelcastConnectionInfo = hazelcastConnectionInfo;
    }

    public String getServiceV2() {
        return serviceV2;
    }

    public void setServiceV2(String serviceV2) {
        this.serviceV2 = serviceV2;
    }

    public String getScopeV2() {
        return scopeV2;
    }

    public void setScopeV2(String scopeV2) {
        this.scopeV2 = scopeV2;
    }

    public String getAudienceV2() {
        return audienceV2;
    }

    public void setAudienceV2(String audienceV2) {
        this.audienceV2 = audienceV2;
    }

    public String getxSwift_Signature_Header() {
        return xSwift_Signature_Header;
    }

    public void setxSwift_Signature_Header(String xSwift_Signature_Header) {
        this.xSwift_Signature_Header = xSwift_Signature_Header;
    }

    public String getClientAliasMgw() {
        return clientAliasMgw;
    }

    public void setClientAliasMgw(String clientAliasMgw) {
        this.clientAliasMgw = clientAliasMgw;
    }

    public String getCallTimeoutSec() {
        return callTimeoutSec;
    }

    public void setCallTimeoutSec(String callTimeoutSec) {
        this.callTimeoutSec = callTimeoutSec;
    }

    public Class<? extends com.swift.sdk.common.api.http.interfaces.HttpBuilderSDK> getHttpBuilderSDK() {
        return HttpBuilderSDK;
    }

    public void setHttpBuilderSDK(Class<? extends com.swift.sdk.common.api.http.interfaces.HttpBuilderSDK> httpBuilderSDK) {
        HttpBuilderSDK = httpBuilderSDK;
    }
}
