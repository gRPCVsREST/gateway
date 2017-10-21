package com.grpcvsrest.restfeed.service;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Selects what {@link AggregatorService} to call based on username.
 *
 * @param <T> service type
 */
final class ServiceSelector<T> {

    private static final HashFunction HASH_FUNCTION = Hashing.murmur3_128();
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final T grpcService;
    private final T restService;

    ServiceSelector(T grpcService, T restService) {
        this.grpcService = grpcService;
        this.restService = restService;
    }

    T chooseService(String username) {
        return username != null ? chooseByHashing(username) : restService;
    }

    private T chooseByHashing(String username) {
        int modulo = HASH_FUNCTION.hashString(username, UTF8).asInt() % 2;
        return modulo == 0 ? grpcService : restService;
    }

}
