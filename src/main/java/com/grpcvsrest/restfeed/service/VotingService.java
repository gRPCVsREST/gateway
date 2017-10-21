package com.grpcvsrest.restfeed.service;

public interface VotingService {

    /**
     * Records user's vote.
     */
    void vote(String username, int itemId, String votedCategory);
}
