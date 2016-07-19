package com.commercetools.ordertomessageprocessor.jobs.actions;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import io.sphere.sdk.orders.Order;

public class MessageWriter implements ItemWriter<Order> {

    @Override
    public void write(List<? extends Order> items) throws Exception {
        for (Order item : items) {
            //TODO handle Message
        }
    }
}
