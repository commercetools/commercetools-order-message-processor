package com.commercetools.ordertomessageprocessor.jobs.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;
import com.commercetools.ordertomessageprocessor.syncinfo.MailSyncInfoHelper;

import io.sphere.sdk.http.HttpClient;
import io.sphere.sdk.http.HttpHeaders;
import io.sphere.sdk.http.HttpMethod;
import io.sphere.sdk.http.HttpRequest;
import io.sphere.sdk.http.HttpRequestBody;
import io.sphere.sdk.http.HttpResponse;
import io.sphere.sdk.http.NameValuePair;
import io.sphere.sdk.http.StringHttpRequestBody;
import io.sphere.sdk.orders.Order;

public class OrderConfirmationMailSender implements ItemWriter<Order> {

    public static final Logger LOG = LoggerFactory.getLogger(OrderConfirmationMailSender.class);

    @Autowired
    HttpClient httpClient;

    @Autowired
    private ConfigurationManager configurationManager;

    @Autowired
    private MailSyncInfoHelper mailSyncInfoHelper;

    @Override
    public void write(List<? extends Order> orders){
        for (Order order : orders) {
            sendMail(order);
        }
    }

    private void sendMail(final Order order) {
        if (configurationManager.getEmailSenderUrl().isPresent()){
            callEmailSenderUrl(order, configurationManager.getEmailSenderUrl().get());
        }
        else {
            //TODO: usage of: commercetools-sunrise-java-email module
        }
    }

    private void callEmailSenderUrl(final Order order, String url) {
        LOG.info("Calling API to send orderconfirmationmail for Order {} and API URL {}", order.getId(), url);
        
        final String body = order.getId();
        final List<NameValuePair> headerList = new ArrayList<NameValuePair>();
        headerList.add(NameValuePair.of(HttpHeaders.CONTENT_TYPE,"text/plain"));
        headerList.add(NameValuePair.of("Content-Length", String.valueOf(body.length())));
        final HttpHeaders httpHeader = HttpHeaders.of(headerList);
        final HttpRequestBody httpBody = StringHttpRequestBody.of(body);
        final HttpRequest httpRequest = HttpRequest.of(HttpMethod.POST, url, httpHeader, httpBody);
        try {
            HttpResponse httpResponse = httpClient.execute(httpRequest).toCompletableFuture().get(40000, TimeUnit.MILLISECONDS);
            if (httpResponse.hasSuccessResponseCode()) {
                LOG.info("Successfully called Shop API to send order confirmation mail for Order {}", order.getId());
                mailSyncInfoHelper.setOrderConfirmationMailWasSend(order);
            }
            else {
                LOG.warn("Could not send Order confirmation mail Response Code from API was {}", httpResponse.getStatusCode());
                mailSyncInfoHelper.setOrderConfirmationMailWasSendWithError(order, "HTTP_RESPONSE:" + httpResponse.getStatusCode());
            }
        }
        catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOG.error("Caught exception {} while calling Shop URL {} to send order confirmation mai for Order {}", e.toString(), url, order.getId());
            //HTTP-Exception. Retry next time
        }
    }
}
