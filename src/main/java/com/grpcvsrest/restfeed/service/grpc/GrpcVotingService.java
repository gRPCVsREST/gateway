package com.grpcvsrest.restfeed.service.grpc;

import com.grpcvsrest.grpc.ResponseType;
import com.grpcvsrest.grpc.VotingRequest;
import com.grpcvsrest.grpc.VotingServiceGrpc.VotingServiceFutureStub;
import com.grpcvsrest.restfeed.service.VotingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Service("grpc-voting-service")
public class GrpcVotingService implements VotingService {

    private final VotingServiceFutureStub votingServiceClient;

    @Autowired
    public GrpcVotingService(VotingServiceFutureStub votingServiceClient) {
        this.votingServiceClient = votingServiceClient;
    }

    @Override
    public void vote(String username, int itemId, String votedCategory) {
        ResponseType voted = ResponseType.valueOf(votedCategory);
        VotingRequest request = VotingRequest.newBuilder()
                .setUsername(username)
                .setItemId(itemId)
                .setVotedCategory(voted)
                .build();
        votingServiceClient.withDeadlineAfter(300, MILLISECONDS).vote(request);
    }
}
