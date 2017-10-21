package com.grpcvsrest.restfeed.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestVotingService {
    private final String url;
    private final RestTemplate restTemplate;

    @Autowired
    public RestVotingService(
            @Value("${rest_voting.url:http://rest-voting:8080}") String url,
            RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    public void vote(String username, int itemId, String votedCategory) {
        Vote vote = new Vote(username, itemId, votedCategory);
        restTemplate.postForEntity(
                url + "/vote/{user_id}/{item_id}",
                vote,
                String.class,
                ImmutableMap.of(
                        "user_id", username,
                        "item_id", itemId
                ));
    }
}
