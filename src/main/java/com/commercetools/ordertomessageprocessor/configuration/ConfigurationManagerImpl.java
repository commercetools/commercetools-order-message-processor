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
    //following are default values if not configured
    private final int defaultItemsPerPage = 100;
    
    //TODO: make overwritable by OS env variable
    private final String defaultContainer = "commercetools-order-to-confirmation-email-processor";
    //TODO: make overwritable by OS env variable
    private final String defaultKey = "configuration";

    @Override
    public void getConfiguration() {
        final CustomObjectQuery<ServiceConfiguration> customObjectQuery = CustomObjectQuery
                .of(ServiceConfiguration.class)
                .byContainer(defaultContainer)
                .plusPredicates(o -> o.key().is(defaultKey));

        final PagedQueryResult<CustomObject<ServiceConfiguration>> result = client.executeBlocking(customObjectQuery);
        
        final List<CustomObject<ServiceConfiguration>> results = result.getResults();
        if (results.isEmpty()) {
            throw new RuntimeException("Could not get configuration from custom-object container " + defaultContainer + " and key " + defaultKey);
        }
        //the commercetools platform asserts that container and key pair are unique
        assert results.size() == 1;
        this.serviceConfiguration = results.get(0).getValue(); 
    }

    @Override
    public int getItemsPerPage() {
        final Integer itemsPerPageFromConfig = serviceConfiguration.getItemsPerPage();
        return itemsPerPageFromConfig != null ? itemsPerPageFromConfig : defaultItemsPerPage;
    }

    @Override
    public String getEmailSenderUrl() {
        return serviceConfiguration.getEmailSenderUrl();
    }
}
