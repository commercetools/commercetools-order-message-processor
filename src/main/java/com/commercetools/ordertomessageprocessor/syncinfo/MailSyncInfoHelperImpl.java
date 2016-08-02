package com.commercetools.ordertomessageprocessor.syncinfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.commands.OrderUpdateCommand;
import io.sphere.sdk.orders.commands.updateactions.UpdateSyncInfo;

/**
 * Helper for reading and writing the SyncInfo to CTP. Which is used for the saving whether a mail was already send. 
 * @author mht@dotsource.de
 *
 */
public class MailSyncInfoHelperImpl implements MailSyncInfoHelper {

    public static final Logger LOG = LoggerFactory.getLogger(MailSyncInfoHelperImpl.class);

    @Autowired
    private BlockingSphereClient client;

    @Autowired
    private ConfigurationManager configurationManager;

    @Override
    public void setOrderConfirmationMailWasSend(final Order order) {
        client.executeBlocking(OrderUpdateCommand.of(order, UpdateSyncInfo.of(configurationManager.getEmailSendChannel())));
    }

    @Override
    public void setOrderConfirmationMailWasSendWithError(final Order order, final String message) {
        client.executeBlocking(OrderUpdateCommand.of(order, UpdateSyncInfo.of(configurationManager.getEmailSendErrorChannel()).withExternalId(message)));
    }
}
