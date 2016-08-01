package com.commercetools.ordertomessageprocessor.configuration;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.WEEKS;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.customobjects.CustomObject;
import io.sphere.sdk.customobjects.queries.CustomObjectQuery;
import io.sphere.sdk.queries.PagedQueryResult;

/**
 * gets configuration from commercetools platform and provides default values for not configured values 
 * @author mht@dotsource.de
 *
 */
public class ConfigurationManagerImpl implements ConfigurationManager{
    public static final Logger LOG = LoggerFactory.getLogger(ConfigurationManagerImpl.class);

    @Autowired
    BlockingSphereClient client; 

    private ServiceConfiguration serviceConfiguration;
    //following are default values if not configured
    private final static int DEFAULTITEMSPERPAGE = 100;
    private final static Duration DEFUALTITEMSOFLAST = Duration.of(5, DAYS);

    private final static String ITMESOFLASTPATTERN = "^[1-9][0-9]*[dhw]$";

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
        return itemsPerPageFromConfig != null ? itemsPerPageFromConfig : DEFAULTITEMSPERPAGE;
    }

    @Override
    public String getEmailSenderUrl() {
        return serviceConfiguration.getEmailSenderUrl();
    }

    @Override
    public Duration getItemsOfLast() {
        final String fromConfiguration = serviceConfiguration.getitemsOfLast();
        //assure pattern matches so we can freely use string operations
        if (fromConfiguration == null){
            return DEFUALTITEMSOFLAST;
        }
        else if (Pattern.matches(ITMESOFLASTPATTERN, fromConfiguration)) {
            //Last char is not part of number
            final int amount = Integer.parseInt(fromConfiguration.substring(0, fromConfiguration.length()-1));
            if (fromConfiguration.endsWith("d")) {
                return Duration.of(amount, DAYS);
            }
            else if (fromConfiguration.endsWith("w")) {
                return Duration.of(amount, WEEKS);
            }
            else {
                return Duration.of(amount, HOURS);
            }
        }
        else {
            LOG.info("The configuration value for \"itemsOfLast\" {} does not match the Pattern {}! Using default-value.", fromConfiguration, ITMESOFLASTPATTERN);
            return DEFUALTITEMSOFLAST;
        }
    }
}
