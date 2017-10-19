package br.com.pedront.bitsotrading.core.feign;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import feign.httpclient.ApacheHttpClient;

@Configuration
@ComponentScan("br.com.ps.mppc.client.api")
public class FeignConfig {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FeignConfig.class);

    @Bean
    public ApacheHttpClient apacheHttpClient() {

        return new ApacheHttpClient(httpClient());

    }

    @Bean
    public HttpClient httpClient() {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        return httpClientBuilder.build();
    }
}