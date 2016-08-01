package com.commercetools.ordertomessageprocessor.jobs;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.commercetools.ordertomessageprocessor.jobs.actions.MessageFilter;
import com.commercetools.ordertomessageprocessor.jobs.actions.OrderConfirmationMailSender;
import com.commercetools.ordertomessageprocessor.jobs.actions.OrderReader;

import io.sphere.sdk.orders.Order;

@Configuration
@EnableBatchProcessing
public class ReadMessagesJob {
    private static final String STEP_LOAD_MESSAGES = "loadMessages";
    
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    @DependsOn("blockingSphereClient")
    public ItemReader<Order> reader() {
        return new OrderReader();
    }

    @Bean
    @DependsOn({"mailSyncInfoHelper", "blockingSphereClient"})
    public ItemProcessor<Order, Order> processor() {
        return new MessageFilter();
    }

    @Bean
    public ItemWriter<Order> writer() {
        return new OrderConfirmationMailSender();
    }

    @Bean
    public JobExecutionListener listener() {
        return new JobListener();
    }

    @Bean
    public Job createJob(@Qualifier(STEP_LOAD_MESSAGES) final Step loadMessages, final JobExecutionListener listener) {
        return jobs.get("readMessagesJob").listener(listener).start(loadMessages).build();
    }

    @Bean
    public Step loadMessages(ItemReader<Order> reader, 
            ItemProcessor<Order, Order> processor,
            ItemWriter<Order> writer) {
        return steps.get(STEP_LOAD_MESSAGES)
                .<Order, Order> chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
