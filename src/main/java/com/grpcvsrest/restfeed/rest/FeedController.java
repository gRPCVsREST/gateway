package com.grpcvsrest.restfeed.rest;

import com.grpcvsrest.restfeed.service.AggregatedContentResponse;
import com.grpcvsrest.restfeed.service.AggregatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeedController {

    private final AggregatorService aggregatorService;

    @Autowired
    public FeedController(
            AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping("/content/{id}")
    public ResponseEntity<FeedItem> feedItem(@PathVariable("id") int id) {
        AggregatedContentResponse aggregatedContent = aggregatorService.fetch(id);
        if (aggregatedContent == null) {
            return ResponseEntity.notFound().build();
        }

        int nextId = aggregatedContent.getId() + 1;
        return ResponseEntity.ok(
                new FeedItem(
                        aggregatedContent.getId(),
                        aggregatedContent.getType(),
                        aggregatedContent.getContent(),
                        "/content/" + nextId));
    }
}
