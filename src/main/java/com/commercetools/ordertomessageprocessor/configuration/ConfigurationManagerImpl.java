package com.commercetools.ordertomessageprocessor.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.queries.CustomObjectQuery;
import io.sphere.sdk.queries.PagedQueryResult;

public class ConfigurationManagerImpl implements ConfigurationManager{

    @Autowired
    BlockingSphereClient client; 

    private ServiceConfiguration serviceConfiguration;
    
    //TODO: make overwritable by OS env variable
    private final String defaultContainerName = "commercetools-order-to-confirmation-email-processor";
    //TODO: make overwritable by OS env variable
    private final String defaultKey = "configuration";

    @Override
    public void getConfiguration() {
        final CustomObjectQuery<ServiceConfiguration> customObjectQuery = CustomObjectQuery
                .of(ServiceConfiguration.class)
                .byContainer(defaultContainerName)
                .plusPredicates(o -> o.key().is(defaultKey));

        final PagedQueryResult<CustomObject<ServiceConfiguration>> result = client.executeBlocking(customObjectQuery);
        
        final List<CustomObject<ServiceConfiguration>> results = result.getResults();
        if (results.isEmpty()) {
            throw new RuntimeException("Could not get configuration from custom-object container " + defaultContainerName + " and key " + defaultKey);
        }
    }
}
