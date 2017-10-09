package com.grpcvsrest.restfeed;

import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceStub;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Application {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AggregationStreamingServiceStub grpcAggrServiceClient(@Value("${grpc_aggregator_host}") String host,
                                                                 @Value("${grpc_aggregator_port}") int port) {
        return AggregationStreamingServiceGrpc.newStub(NettyChannelBuilder
                .forAddress(host, port).usePlaintext(true).build());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
