package com.rackspacecloud.blueflood.kafkametricsmetadataconsumer;

import com.rackspacecloud.blueflood.kafkametricsmetadataconsumer.provider.IAuthTokenProvider;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class BluefloodMetricsMetadataListener {
    IAuthTokenProvider authTokenProvider;
    RestTemplate restTemplate;

    private long messageProcessedCount = 0;

    // At the end of every 1000 messages, log this information
    private static final int MESSAGE_PROCESS_REPORT_COUNT = 1000;
    private static final String X_AUTH_TOKEN = "x-auth-token";

    @Value("${metadata-service.url}")
    private String metadataServiceUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(BluefloodMetricsMetadataListener.class);

    @Autowired
    public BluefloodMetricsMetadataListener(IAuthTokenProvider authTokenProvider, RestTemplate restTemplate) {
        this.authTokenProvider = authTokenProvider;
        this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "#{'${kafka.topics.in}'.split(',')}")
    public void listen(final ConsumerRecord<String, String> record) {
        LOGGER.debug("Received record:{}", record);

        String payload = record.value();

        HttpEntity<String> httpEntity = new HttpEntity<>(payload, getHttpHeaders());
        String metricsIngestUrl = String.format("%s/metrics/ingest", metadataServiceUrl);

        ResponseEntity<String> response = null;

        response = restTemplate.exchange(metricsIngestUrl, HttpMethod.POST, httpEntity, String.class);

        if(response == null){
            LOGGER.error("Using metadata-service url [{}], got null in response for the payload -> {}",
                    metricsIngestUrl, payload);
        }
        else if(response.getStatusCode() != HttpStatus.OK){
            LOGGER.error("Using metadata-service url [{}], couldn't ingest the payload -> {}",
                    metricsIngestUrl, payload);
        }
        else{
            LOGGER.info("Successfully ingested payload in Blueflood -> {}", payload);
        }
    }

    private HttpHeaders getHttpHeaders(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypes);

        headers.set(X_AUTH_TOKEN, authTokenProvider.getAuthToken());

        return headers;
    }
}
