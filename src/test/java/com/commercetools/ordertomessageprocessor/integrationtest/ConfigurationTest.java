package com.commercetools.ordertomessageprocessor.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.commercetools.ordertomessageprocessor.ShereClientConfiguration;
import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;
import com.commercetools.ordertomessageprocessor.configuration.ServiceConfiguration;
import com.commercetools.ordertomessageprocessor.testconfiguration.BasicTestConfiguration;
import com.commercetools.ordertomessageprocessor.testconfiguration.ConfigurationManagerConfiguration;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.CustomObjectDraft;
import io.sphere.sdk.customobjects.commands.CustomObjectUpsertCommand;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {BasicTestConfiguration.class, ShereClientConfiguration.class, ConfigurationManagerConfiguration.class}, initializers = ConfigFileApplicationContextInitializer.class)
public class ConfigurationTest {

    @Autowired
    private BlockingSphereClient client;

    @Autowired
    private ConfigurationManager configurationManager;
    
    private final String defaultContainer = "commercetools-order-to-confirmation-email-processor";
    private final String defaultKey = "configuration";

    @Test
    public void testDefaultValuesForEmptyConfiguratin() {
        final ServiceConfiguration serviceConfiguration =  new ServiceConfiguration("SomeURL", null);
        final CustomObjectUpsertCommand<ServiceConfiguration> createCustomObject = CustomObjectUpsertCommand.of(
                CustomObjectDraft.ofUnversionedUpsert(defaultContainer, defaultKey, serviceConfiguration, ServiceConfiguration.class));
        final CustomObject<ServiceConfiguration> customObject = client.executeBlocking(createCustomObject);
        System.out.println(customObject);

        configurationManager.getConfiguration();

        assertThat(configurationManager.getItemsPerPage()).isEqualTo(100);
    }
}
