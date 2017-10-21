package com.grpcvsrest.restfeed.service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

/**
 * Selects what {@link AggregatorService} to call.
 */
@Service("aggr-service")
public class AggregatorServiceSelector implements AggregatorService {

    private static final HashFunction HASH_FUNCTION = Hashing.murmur3_128();
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private final AggregatorService grpcService;
    private final AggregatorService restService;

    @Autowired
    public AggregatorServiceSelector(
            @Qualifier("grpc-aggr-service") AggregatorService grpcService,
            @Qualifier("rest-aggr-service") AggregatorService restService) {
        this.grpcService = grpcService;
        this.restService = restService;
    }

    @Override
    public AggregatedContentResponse fetch(Integer id, String username) {
        return chooseService(username).fetch(id, username);
    }

    private AggregatorService chooseService(String username) {
        return username != null ? chooseByHashing(username) : restService;
    }

    private AggregatorService chooseByHashing(String username) {
        int modulo = HASH_FUNCTION.hashString(username, UTF8).asInt() % 2;
        return modulo == 0 ? grpcService : restService;
    }
}
