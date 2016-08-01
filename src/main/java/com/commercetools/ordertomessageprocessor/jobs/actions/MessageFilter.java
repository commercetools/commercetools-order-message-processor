package com.commercetools.ordertomessageprocessor.jobs.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import io.sphere.sdk.orders.Order;

/**
 * Orderfilter. Spring Boot filter (processor). At the moment not need to further filter 
 * @author mht@dotsource.de
 *
 */
public class MessageFilter implements ItemProcessor<Order, Order> {
    public static final Logger LOG = LoggerFactory.getLogger(MessageFilter.class);

    @Override
    public Order process(Order order) {
        return order;
    }
}
