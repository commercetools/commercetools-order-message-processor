package com.commercetools.ordertomessageprocessor.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.commercetools.ordertomessageprocessor.configuration.ConfigurationManager;

public class JobListener implements JobExecutionListener {

    public static final Logger LOG = LoggerFactory.getLogger(JobListener.class);

    @Autowired
    private ConfigurationManager configurationManager;

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() != BatchStatus.COMPLETED) {
            LOG.error("Job did not complete. BatchStatus is {}", jobExecution.getStatus());
        }
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        LOG.info("Getting Configuration values from commercetools platform");
        configurationManager.getConfiguration();
    }
}
