package com.grpcvsrest.restfeed;

import brave.SpanCustomizer;
import brave.Tracing;
import brave.grpc.GrpcClientParser;
import brave.grpc.GrpcTracing;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceStub;
import com.grpcvsrest.grpc.VotingServiceGrpc;
import com.grpcvsrest.grpc.VotingServiceGrpc.VotingServiceFutureStub;
import io.grpc.CallOptions;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import io.grpc.netty.NettyChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.sleuth.SpanAdjuster;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import static brave.sampler.Sampler.ALWAYS_SAMPLE;

@SpringBootApplication
public class Application {

    @Bean
    public SpanAdjuster customSpanAdjuster(@Value("${spring.application.name}") String appName) {
        return span -> span.toBuilder().name("#" + appName + "/" + span.getName().replace("http:/", "")).build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public GrpcTracing grpcTracing(@Value("${zipkin_service_host:zipkin}") String zipkinHost,
                                   @Value("${zipkin_service_port:9411}") int zipkinPort,
                                   Tracer tracer) {

        URLConnectionSender sender = URLConnectionSender.newBuilder()
                .endpoint(String.format("http://%s:%s/api/v2/spans", zipkinHost, zipkinPort))
                .build();

        return GrpcTracing.newBuilder(Tracing.newBuilder()
                .sampler(ALWAYS_SAMPLE)
                .spanReporter(AsyncReporter.create(sender))
                .build())
                .clientParser(new GrpcClientParser() {
                    @Override
                    protected <M> void onMessageSent(M message, SpanCustomizer span) {
                        span.name(tracer.getCurrentSpan().getName());
                    }

                    @Override
                    protected <ReqT, RespT> void onStart(MethodDescriptor<ReqT, RespT> method, CallOptions options,
                                                         Metadata headers, SpanCustomizer span) {
                        span.tag("grpc.method", method.getFullMethodName());
                        span.name("#grpc." + method.getFullMethodName());
                    }
                })
                .build();
    }

    @Bean
    public AggregationStreamingServiceStub grpcAggrServiceClient(@Value("${grpc_aggregator_host}") String host,
                                                                 @Value("${grpc_aggregator_port}") int port,
                                                                 GrpcTracing grpcTracing) {

        return AggregationStreamingServiceGrpc.newStub(NettyChannelBuilder
                .forAddress(host, port)
                .intercept(grpcTracing.newClientInterceptor())
                .usePlaintext(true).build());
    }

    @Bean
    public VotingServiceFutureStub grpcVotingServiceClient(@Value("${grpc_voting_host}") String host,
                                                           @Value("${grpc_voting_port}") int port,
                                                           GrpcTracing grpcTracing) {
        return VotingServiceGrpc.newFutureStub(NettyChannelBuilder
                .forAddress(host, port)
                .intercept(grpcTracing.newClientInterceptor())
                .usePlaintext(true).build());
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
