package com.grpcvsrest.restfeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Selects what {@link VotingService} to call.
 */
@Service("voting-service")
public class VotingServiceSelector implements VotingService {

    private final ServiceSelector<VotingService> serviceSelector;

    @Autowired
    public VotingServiceSelector(
            @Qualifier("grpc-voting-service") VotingService grpcService,
            @Qualifier("rest-voting-service") VotingService restService) {
        this.serviceSelector = new ServiceSelector<>(grpcService, restService);
    }

    @Override
    public void vote(String username, int itemId, String votedCategory) {
        serviceSelector.chooseService(username).vote(username, itemId, votedCategory);
    }
}
