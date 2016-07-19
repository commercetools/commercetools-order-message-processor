package com.commercetools.ordertomessageprocessor.jobs.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.messages.OrderCreatedMessage;


public class MessageProcessor implements ItemProcessor<OrderCreatedMessage, Order> {
    public static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);
    
    @Override
    public Order process(OrderCreatedMessage message) {
        LOG.info("Called MessageProcesser.process with parameter {}", message);
        return message.getOrder();
    }
}
