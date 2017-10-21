package com.grpcvsrest.restfeed.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Selects what {@link AggregatorService} to call.
 */
@Service("aggr-service")
public class AggregatorServiceSelector implements AggregatorService {

    private final ServiceSelector<AggregatorService> serviceSelector;

    @Autowired
    public AggregatorServiceSelector(
            @Qualifier("grpc-aggr-service") AggregatorService grpcService,
            @Qualifier("rest-aggr-service") AggregatorService restService) {
        this.serviceSelector = new ServiceSelector<>(grpcService, restService);
    }

    @Override
    public AggregatedContentResponse fetch(Integer id, String username) {
        return serviceSelector.chooseService(username).fetch(id, username);
    }
}
