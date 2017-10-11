package com.grpcvsrest.restfeed.rest;

import org.springframework.web.bind.annotation.*;

@RestController
public class VotingController {

    @PutMapping("/vote/{user_id}/{item_id}/{voted_category}")
    public void vote(
            @RequestHeader(value = "username", required = false) String username,
            @PathVariable("user_id") String userId,
            @PathVariable("item_id") int itemId,
            @PathVariable("voted_category") String votedCategory) {

    }
}
