package com.commercetools.ordertomessageprocessor.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.sphere.sdk.models.Base;

/**
 * POJO for the custom object that saves the service configuration in the commercetools platform
 * @author mht@dotsource.de
 *
 */
public class ServiceConfiguration extends Base {
    private String emailSenderUrl;
    private Integer itemsPerPage;

    @JsonCreator
    public ServiceConfiguration(
            @JsonProperty("emailSenderUrl") final String emailSenderUrl,
            @JsonProperty("itemsPerPage")final Integer itemsPerPage) {
        this.emailSenderUrl = emailSenderUrl;
        this.itemsPerPage = itemsPerPage;
    }

    public String getEmailSenderUrl() {
        return emailSenderUrl;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }
}
