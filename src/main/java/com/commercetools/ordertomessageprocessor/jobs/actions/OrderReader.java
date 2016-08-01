package com.commercetools.ordertomessageprocessor.jobs.actions;

import static java.time.temporal.ChronoUnit.HOURS;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;

import io.sphere.sdk.channels.Channel;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.queries.OrderQuery;
import io.sphere.sdk.queries.PagedQueryResult;

/**
 * The OrderReader fetches Orders from the commercetools platform and checks if orderconfirmationmail was not send yet.
 * @author mht@dotsource.de
 *
 */
public class OrderReader implements ItemReader<Order> {

    public static final Logger LOG = LoggerFactory.getLogger(OrderReader.class);

    @Autowired
    private BlockingSphereClient client;

    @Autowired
    private ConfigurationManager configurationManager;

    private List<Order> orders = Collections.emptyList();
    private boolean wasInitialQueried = false;
    private long total;
    private long offset = 0;

    private OrderQuery orderQuery;
    
    private final ZonedDateTime now = ZonedDateTime.now();

    //TODO: make this configurable
    private final Duration readtime = Duration.of(120, HOURS);

    @Override
    public Order read() {
        if(isQueryNeeded()) {
            queryPlatform();
        }
        return getMessageFromList();
    }

    private Order getMessageFromList() {
        if (orders.isEmpty()){
            return null;
        }
        else{
            return orders.remove(0);
        }
    }

    private boolean isQueryNeeded() {
        if (!wasInitialQueried) {
            return true;
        }
        
        return (orders.isEmpty() && total > offset);
    }

    private void queryPlatform(){
        LOG.info("Query CTP for Orders");
        buildQuery();
        final PagedQueryResult<Order> result = client.executeBlocking(orderQuery);
        total = result.getTotal();
        offset = result.getOffset() + result.getCount();
        orders = result.getResults();
        wasInitialQueried = true;
    }

    private void buildQuery(){
        //TODO GET Channels
        final Channel reference1 = null;
        final Channel reference2 = null;
        final List<Channel> channels = new ArrayList<Channel>();
        channels.add(reference1);
        channels.add(reference2);
        orderQuery = OrderQuery.of()
                .withPredicates(order -> order.syncInfo().channel().isIn(channels).negate())
                .plusPredicates(order -> order.lastModifiedAt().isGreaterThanOrEqualTo(now.minus(readtime)))
                .withSort(m -> m.lastModifiedAt().sort().asc())
                .withOffset(offset)
                .withLimit(configurationManager.getItemsPerPage());
    }
}
