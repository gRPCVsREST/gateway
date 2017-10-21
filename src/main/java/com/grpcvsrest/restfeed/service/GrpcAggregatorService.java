package com.grpcvsrest.restfeed.service;

import com.grpcvsrest.grpc.AggregationStreamingRequest;
import com.grpcvsrest.grpc.AggregationStreamingResponse;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceStub;
import io.grpc.ClientCall;
import io.grpc.ClientCall.Listener;
import io.grpc.Metadata;
import io.grpc.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * gRPC implementation of {@link AggregatorService}.
 */
@Service("grpc-aggr-service")
public class GrpcAggregatorService implements AggregatorService {

    private final ConcurrentMap<String, BlockingQueue<ResponseOrError>> userToQueue =
            new ConcurrentHashMap<>();
    private final AggregationStreamingServiceStub stub;

    @Autowired
    public GrpcAggregatorService(AggregationStreamingServiceStub stub) {
        this.stub = stub;
    }

    @Override
    public AggregatedContentResponse fetch(Integer id, String username) {

        BlockingQueue<ResponseOrError> newQueue = new LinkedBlockingQueue<>();
        BlockingQueue<ResponseOrError> existing = userToQueue.putIfAbsent(username, newQueue);
        BlockingQueue<ResponseOrError> queue;
        if (existing == null) {
            subscribeWithFlowControl(username, newQueue);
            queue = newQueue;
        } else {
            queue = existing;
        }
        AggregationStreamingResponse response;
        try {
            response = queue.take().getOrThrow();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AggregatedContentResponse(
                response.getId(),
                response.getType().name(),
                response.getContent(),
                "/content");
    }

    private void subscribeWithFlowControl(String username, Queue<ResponseOrError> queue) {
        ClientCall<AggregationStreamingRequest, AggregationStreamingResponse> call =
                stub.getChannel().newCall(AggregationStreamingServiceGrpc.METHOD_SUBSCRIBE, stub.getCallOptions());
        call.start(new Listener<AggregationStreamingResponse>() {

            @Override
            public void onMessage(AggregationStreamingResponse message) {
                queue.add(ResponseOrError.create(message, call));
            }

            @Override
            public void onClose(Status status, Metadata trailers) {
                userToQueue.remove(username);
                if (!status.isOk()) {
                    queue.add(ResponseOrError.create(status.asRuntimeException(trailers)));
                }
            }
        }, new Metadata());
        call.request(1);
        call.sendMessage(AggregationStreamingRequest.getDefaultInstance());
        call.halfClose();
    }

    private static class ResponseOrError {
        private final AggregationStreamingResponse response;
        private final RuntimeException error;
        private final ClientCall<?, ?> clientCall;

        private ResponseOrError(AggregationStreamingResponse response,
                                RuntimeException error,
                                ClientCall<?, ?> clientCall) {
            this.response = response;
            this.error = error;
            this.clientCall = clientCall;
        }

        private static ResponseOrError create(AggregationStreamingResponse response, ClientCall<?, ?> clientCall) {
            return new ResponseOrError(response, null, clientCall);
        }

        private static ResponseOrError create(RuntimeException error) {
            return new ResponseOrError(null, error, null);
        }

        private AggregationStreamingResponse getOrThrow() {
            if (error != null) {
                throw error;
            }
            clientCall.request(1);
            return response;
        }
    }

}
