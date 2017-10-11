package com.grpcvsrest.restfeed.service;

/**
 * Service that provides aggregations by id.
 */
public interface AggregatorService {

    AggregatedContentResponse fetch(Integer id, String username);

}
