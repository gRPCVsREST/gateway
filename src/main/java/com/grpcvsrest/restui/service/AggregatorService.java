package com.grpcvsrest.restui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AggregatorService {
    private final String url;
    private final RestTemplate restTemplate;

    @Autowired
    public AggregatorService(
            @Value("${rest_aggregator.url}") String url,
            RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public AggregatedContent fetch(int id) {
        ResponseEntity<AggregatedContent> entity = restTemplate.getForEntity(
                url + "/content/{id}",
                AggregatedContent.class,
                id);

        if (entity.getStatusCode().value() == 404) {
            return null;
        }
        return entity.getBody();
    }
}
