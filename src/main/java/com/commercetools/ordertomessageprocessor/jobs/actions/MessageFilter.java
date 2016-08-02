package com.commercetools.ordertomessageprocessor.jobs.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.commercetools.ordertomessageprocessor.syncinfo.MailSyncInfoHelper;

import io.sphere.sdk.orders.Order;

/**
 * Orderfilter. Spring Boot filter (processor). 
 * @author mht@dotsource.de
 *
 */
public class MessageFilter implements ItemProcessor<Order, Order> {
    public static final Logger LOG = LoggerFactory.getLogger(MessageFilter.class);

    @Autowired
    private MailSyncInfoHelper mailSyncInfoHelper; 

    private static final String NOEMAILADDRESS = "noEmailAddress";

    @Override
    public Order process(Order order) {
        if (order.getCustomerEmail() != null) {
            return order;
        }
        else {
            LOG.error("There is no CustomerEmail configured for Order {}", order.getId());
            mailSyncInfoHelper.setOrderConfirmationMailWasSendWithError(order, NOEMAILADDRESS);
            return null;
        }
    }
}
