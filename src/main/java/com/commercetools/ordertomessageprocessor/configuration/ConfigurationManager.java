package com.commercetools.ordertomessageprocessor.configuration;

import java.time.Duration;

import io.sphere.sdk.channels.Channel;

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
    public String getEmailSendChannelKey();
    public String getEmailSendErrorChannelKey();
    public Channel getEmailSendChannel();
    public Channel getEmailSendErrorChannel();
}
