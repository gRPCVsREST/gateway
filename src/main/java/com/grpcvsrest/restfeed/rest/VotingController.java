package com.grpcvsrest.restfeed.rest;

import com.grpcvsrest.restfeed.service.RestVotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class VotingController {

    private final RestVotingService restVotingService;

    @Autowired
    public VotingController(RestVotingService restVotingService) {
        this.restVotingService = restVotingService;
    }

    @PutMapping("/vote/{user_id}/{item_id}/{voted_category}")
    public void vote(
            @RequestHeader(value = "username", required = false) String username,
            @PathVariable("user_id") String userId,
            @PathVariable("item_id") int itemId,
            @PathVariable("voted_category") String votedCategory) {
        restVotingService.vote(userId, itemId, votedCategory);
    }
}
