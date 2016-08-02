package com.commercetools.ordertomessageprocessor.testconfiguration;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.commercetools.ordertomessageprocessor.SphereConfiguration;

import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereAccessTokenSupplier;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientConfig;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.http.HttpClient;

@Configuration
public class BasicTestConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Bean 
    public HttpClient httpClient() {
        return SphereClientFactory.of().createHttpClient();
    }
    
    @Bean
    @DependsOn({"sphereConfiguration", "httpClient"})
    public BlockingSphereClient blockingSphereClient(final SphereConfiguration config, final HttpClient httpClient) {
        final SphereClientConfig clientConfig = SphereClientConfig.of(
                config.getProjectKey(),
                config.getClientId(),
                config.getClientSecret(),
                config.ctpAuthUrl(),
                config.ctpApiUrl());
        final SphereAccessTokenSupplier sphereAccessTokenSupplierWithAutoRefresh = SphereAccessTokenSupplier.ofAutoRefresh(clientConfig, httpClient, false);
        //lightweight client
        final SphereClient sphereClient = SphereClient.of(clientConfig, httpClient, sphereAccessTokenSupplierWithAutoRefresh);

        return BlockingSphereClient.of(sphereClient, config.getDefaultTimeout(), TimeUnit.MILLISECONDS);
    }
}
