package com.commercetools.ordertomessageprocessor.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;

import io.sphere.sdk.models.Base;

/**
 * POJO for the custom object that saves the service configuration in the commercetools platform
 * @author mht@dotsource.de
 *
 */
public class ServiceConfiguration extends Base {
    private String emailSenderUrl;
    
    @JsonCreator
    public ServiceConfiguration(final String emailSenderUrl) {
        this.emailSenderUrl = emailSenderUrl;
    }
    
    public String getEmailSenderUrl() {
        return emailSenderUrl;
    }
}
