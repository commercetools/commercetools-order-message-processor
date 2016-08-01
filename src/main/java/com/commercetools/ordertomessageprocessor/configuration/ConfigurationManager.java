package com.commercetools.ordertomessageprocessor.configuration;

import java.time.Duration;

/**
 * Manages the service configuration
 * @author mht@dotsource.de
 *
 */
public interface ConfigurationManager {

    public void getConfiguration();
    public int getItemsPerPage();
    public String getEmailSenderUrl();
    public Duration getItemsOfLast();
}
