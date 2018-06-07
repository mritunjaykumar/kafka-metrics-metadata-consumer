package com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.config;

import com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.provider.AuthTokenProvider;
import com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.provider.IAuthTokenProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfiguration {
    @Autowired
    RestTemplateConfigurationProperties config;

    // Create instance of Connection Pooling Manager
    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager(){
        PoolingHttpClientConnectionManager poolingConnectionManager =
                new PoolingHttpClientConnectionManager();

        poolingConnectionManager.setMaxTotal(config.getPoolingHttpClientConnectionManager().getMaxTotal());
        return poolingConnectionManager;
    }

    // Create instance of RequestConfig
    @Bean
    public RequestConfig requestConfig(){
        return RequestConfig.custom()
                .setConnectionRequestTimeout(config.getRequestConfig().getConnectionRequestTimeout())
                .setConnectTimeout(config.getRequestConfig().getConnectTimeout())
                .setSocketTimeout(config.getRequestConfig().getSocketTimeout())
                .build();
    }

    // Create instance of HttpClient
    @Bean
    public CloseableHttpClient httpClient(){
        return HttpClientBuilder.create()
                .setConnectionManager(poolingHttpClientConnectionManager())
                .setDefaultRequestConfig(requestConfig())
                .build();
    }

    // Create instance of RestTemplate
    @Bean
    public RestTemplate restTemplate(){
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient());

        return new RestTemplate(requestFactory);
    }

    // Create instance of Auth Token Provider
    @Bean
    public IAuthTokenProvider authTokenProvider(){
        return new AuthTokenProvider(restTemplate(), config);
    }
}
