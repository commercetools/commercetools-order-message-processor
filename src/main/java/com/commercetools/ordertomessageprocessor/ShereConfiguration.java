package com.commercetools.ordertomessageprocessor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShereConfiguration {

    @Value("${ctpClientId}")
    private String clientId;
    @Value("${ctpClientSecret}")
    private String clientSecret;
    @Value("${ctpProjectKey}")
    private String projectKey;
    @Value("${ctpAuthUrl}")
    private String ctpAuthUrl;
    @Value("${ctpApiUrl}")
    private String ctpApiUrl;
    @Value("${ctp.timeout.default}")
    private Integer defaultTimeout;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String ctpAuthUrl() {
        return ctpAuthUrl;
    }

    public String ctpApiUrl() {
        return ctpApiUrl;
    }

    public Integer getDefaultTimeout() {
        return defaultTimeout;
    }
}
