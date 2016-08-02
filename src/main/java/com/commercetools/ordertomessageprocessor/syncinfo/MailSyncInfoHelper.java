package com.commercetools.ordertomessageprocessor.syncinfo;

import io.sphere.sdk.orders.Order;

public interface MailSyncInfoHelper {
    public void setOrderConfirmationMailWasSend(final Order order);
    public void setOrderConfirmationMailWasSendWithError(final Order order, final String messge);
}
