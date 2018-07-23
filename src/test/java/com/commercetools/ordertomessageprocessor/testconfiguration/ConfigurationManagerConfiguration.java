package com.commercetools.ordertomessageprocessor.testconfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;
import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManagerImpl;

@Configuration
public class ConfigurationManagerConfiguration {

    @Bean
    public static ConfigurationManager configurationManager() {
       return new ConfigurationManagerImpl();
    }
}
