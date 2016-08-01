package com.commercetools.ordertomessageprocessor.jobs.actions;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import io.sphere.sdk.orders.Order;

public class OrderConfirmationMailSender implements ItemWriter<Order> {

    @Override
    public void write(List<? extends Order> orders) throws Exception {
        for (Order order : orders) {
            //TODO send Mail
        }
    }
}
