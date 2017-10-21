package com.grpcvsrest.restfeed.rest;

import com.grpcvsrest.restfeed.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VotingController {

    private final VotingService restVotingService;

    @Autowired
    public VotingController(@Qualifier("voting-service") VotingService restVotingService) {
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
