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

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;
import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManagerImpl;
import com.commercetools.ordertomessageprocessor.jobs.actions.MessageFilter;
import com.commercetools.ordertomessageprocessor.jobs.actions.OrderConfirmationMailSender;
import com.commercetools.ordertomessageprocessor.jobs.actions.OrderReader;
import com.commercetools.ordertomessageprocessor.syncinfo.MailSyncInfoHelper;
import com.commercetools.ordertomessageprocessor.syncinfo.MailSyncInfoHelperImpl;

import io.sphere.sdk.orders.Order;

@Configuration
@EnableBatchProcessing
public class ReadOrdersAndSendMailJob {
    private static final String STEP_LOAD_MESSAGES = "readOrdersAndSendMailStep";
    
    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public ItemReader<Order> reader() {
        return new OrderReader();
    }

    @Bean
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
    public ConfigurationManager configurationManager() {
        return new ConfigurationManagerImpl();
    }

    @Bean
    public MailSyncInfoHelper mailSyncInfoHelper() {
        return new MailSyncInfoHelperImpl();
    }

    @Bean
    public Job createJob(@Qualifier(STEP_LOAD_MESSAGES) final Step readOrdersAndSendMailStep, final JobExecutionListener listener) {
        return jobs.get("ReadOrdersAndSendMailJob").listener(listener).start(readOrdersAndSendMailStep).build();
    }

    @Bean
    public Step readOrdersAndSendMailStep(ItemReader<Order> reader, 
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
