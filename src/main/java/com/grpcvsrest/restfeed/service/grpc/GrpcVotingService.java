package com.grpcvsrest.restfeed.service.grpc;

import com.grpcvsrest.restfeed.service.VotingService;
import org.springframework.stereotype.Service;

@Service("grpc-voting-service")
public class GrpcVotingService implements VotingService {

    @Override
    public void vote(String username, int itemId, String votedCategory) {

    }
}
