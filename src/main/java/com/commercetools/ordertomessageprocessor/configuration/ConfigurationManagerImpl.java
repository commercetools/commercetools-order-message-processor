package com.commercetools.ordertomessageprocessor.configuration;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

import java.time.Duration;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.channels.ChannelDraft;
import io.sphere.sdk.channels.ChannelRole;
import io.sphere.sdk.channels.commands.ChannelCreateCommand;
import io.sphere.sdk.channels.queries.ChannelQuery;
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

    //values stored here are created when calling getConfiguration
    private ServiceConfiguration serviceConfiguration;
    private Channel emailSendChannel; 
    private Channel emailSendErrorChannel;
    
    //following are default values if not configured
    private final static int DEFAULTITEMSPERPAGE = 100;
    private final static Duration DEFUALTITEMSOFLAST = Duration.of(5, DAYS);
    private final static String DEFAULTEMAILSENTCHANNELKEY = "orderConfirmationEmail";
    private final static String DEFAULTEMAILSENTCHANNELERRORKEY = "orderConfirmationEmailError";

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

        getOrCreateChannels();
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
                return Duration.of(amount * 7, DAYS);
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

    @Override
    public String getEmailSendChannelKey() {
        final String emailSentChannelKey = serviceConfiguration.getEmailSentChannelKey();
        return emailSentChannelKey != null ? emailSentChannelKey : DEFAULTEMAILSENTCHANNELKEY;
    }

    @Override
    public String getEmailSendErrorChannelKey() {
        final String emailSentErrorChannelKey = serviceConfiguration.getEmailSendErrorChannelKey();
        return emailSentErrorChannelKey != null ? emailSentErrorChannelKey : DEFAULTEMAILSENTCHANNELERRORKEY;
    }

    @Override
    public Channel getEmailSendChannel() {
        return emailSendChannel;
    }

    @Override
    public Channel getEmailSendErrorChannel() {
        return emailSendErrorChannel;
    }

    private void getOrCreateChannels() {
        emailSendChannel = getOrCreateChannel(getEmailSendChannelKey()); 
        emailSendErrorChannel = getOrCreateChannel(getEmailSendErrorChannelKey());
    }

    private Channel getOrCreateChannel(final String channelName) {
        final PagedQueryResult<Channel> result = client.executeBlocking(ChannelQuery.of().byKey(channelName));
        final List<Channel> results = result.getResults();
        if (results.isEmpty()) {
            return client.executeBlocking(ChannelCreateCommand.of(ChannelDraft.of(channelName).withRoles(ChannelRole.ORDER_EXPORT)));
        }
        else {
            assert results.size() == 1;
            return results.get(0);
        }
    }
}
