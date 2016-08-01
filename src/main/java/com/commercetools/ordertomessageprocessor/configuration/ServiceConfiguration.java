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
    private String itemsOfLast;

    @JsonCreator
    public ServiceConfiguration(
            @JsonProperty("emailSenderUrl") final String emailSenderUrl,
            @JsonProperty("itemsPerPage") final Integer itemsPerPage,
            @JsonProperty("itemsOfLast") final String itemsOfLast) {
        this.emailSenderUrl = emailSenderUrl;
        this.itemsPerPage = itemsPerPage;
        this.itemsOfLast = itemsOfLast;
    }

    public String getEmailSenderUrl() {
        return emailSenderUrl;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public String getitemsOfLast() {
        return itemsOfLast;
    }
}
