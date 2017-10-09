package com.grpcvsrest.restfeed.rest;

import com.grpcvsrest.restfeed.service.AggregatedContentResponse;
import com.grpcvsrest.restfeed.service.AggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private final AggregatorService aggregatorService;

    @Autowired
    public FeedController(
            @Qualifier("aggr-service") AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<FeedItem> feedItem(@PathVariable("id") int id,
                                             @RequestHeader(value = "username", required = false) String username) {
        AggregatedContentResponse aggregatedContent = aggregatorService.fetch(id, username);
        if (aggregatedContent == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                new FeedItem(
                        aggregatedContent.getId(),
                        aggregatedContent.getType(),
                        aggregatedContent.getContent(),
                        aggregatedContent.getNextUri()));
    }
}
