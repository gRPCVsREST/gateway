package com.grpcvsrest.restfeed.service.grpc;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.grpcvsrest.grpc.AggregationStreamingRequest;
import com.grpcvsrest.grpc.AggregationStreamingResponse;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc;
import com.grpcvsrest.grpc.AggregationStreamingServiceGrpc.AggregationStreamingServiceStub;
import com.grpcvsrest.restfeed.service.AggregatedContentResponse;
import com.grpcvsrest.restfeed.service.AggregatorService;
import io.grpc.ClientCall;
import io.grpc.ClientCall.Listener;
import io.grpc.Metadata;
import io.grpc.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * gRPC implementation of {@link AggregatorService}.
 */
@Service("grpc-aggr-service")
public class GrpcAggregatorService implements AggregatorService {

    private final Cache<String, CallAndQueue> userToCallAndQueue =
            CacheBuilder.newBuilder()
                    .expireAfterAccess(10, TimeUnit.MINUTES)
                    .maximumSize(10_000)
                    .<String, CallAndQueue>removalListener(removalNotification -> {
                        RemovalCause cause = removalNotification.getCause();
                        if (cause == RemovalCause.EXPIRED || cause == RemovalCause.SIZE) {
                            CallAndQueue call = removalNotification.getValue();
                            call.cancelCall();
                        }
                    })
                    .build();

    private final AggregationStreamingServiceStub stub;

    @Autowired
    public GrpcAggregatorService(AggregationStreamingServiceStub stub) {
        this.stub = stub;
    }

    @Override
    public AggregatedContentResponse fetch(Integer id, String username) {

        CallAndQueue newCallAndQueue = new CallAndQueue();
        CallAndQueue existing = userToCallAndQueue.asMap().putIfAbsent(username, newCallAndQueue);
        CallAndQueue callAndQueue;
        if (existing == null) {
            subscribeWithFlowControl(username, newCallAndQueue);
            callAndQueue = newCallAndQueue;
        } else {
            callAndQueue = existing;
        }
        AggregationStreamingResponse response;
        try {
            response = callAndQueue.take().getOrThrow();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new AggregatedContentResponse(
                response.getId(),
                response.getType().name(),
                response.getContent(),
                "/content");
    }

    private void subscribeWithFlowControl(String username, CallAndQueue queue) {
        ClientCall<AggregationStreamingRequest, AggregationStreamingResponse> call =
                stub.getChannel().newCall(AggregationStreamingServiceGrpc.METHOD_SUBSCRIBE, stub.getCallOptions());
        call.start(new Listener<AggregationStreamingResponse>() {

            @Override
            public void onMessage(AggregationStreamingResponse message) {
                queue.add(ResponseOrError.create(message, call));
            }

            @Override
            public void onClose(Status status, Metadata trailers) {
                userToCallAndQueue.invalidate(username);
                if (!status.isOk()) {
                    queue.add(ResponseOrError.create(status.asRuntimeException(trailers)));
                }
            }
        }, new Metadata());
        queue.setCall(call);
        call.request(1);
        call.sendMessage(AggregationStreamingRequest.getDefaultInstance());
        call.halfClose();
    }

    private static class CallAndQueue {
        private final AtomicReference<ClientCall<?, ?>> clientCallRef = new AtomicReference<>();
        private final BlockingQueue<ResponseOrError> queue = new LinkedBlockingQueue<>();

        private ResponseOrError take() throws InterruptedException {
            return queue.take();
        }

        private void add(ResponseOrError responseOrError) {
            queue.add(responseOrError);
        }

        private void setCall(ClientCall<?, ?> call) {
            if (clientCallRef.get() != null) {
                throw new IllegalStateException("Client call has already been set.");
            }
            clientCallRef.set(call);
        }

        public void cancelCall() {
            ClientCall<?, ?> clientCall = clientCallRef.get();
            if (clientCall != null) {
                clientCall.cancel("Haven't seen user for a long time, connection evicted.", null);
            }
        }

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
