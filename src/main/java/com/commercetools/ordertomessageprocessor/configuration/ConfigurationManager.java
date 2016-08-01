package com.commercetools.ordertomessageprocessor.configuration;

public interface ConfigurationManager {

    public void getConfiguration();
    public int getItemsPerPage();
    public String getEmailSenderUrl();
}
