package de.stroeer.paperboy_light.interceptors;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionInterceptor implements ServerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        ServerCall.Listener<ReqT> delegate = null;
        try {
            delegate = next.startCall(call, headers);
        } catch (RuntimeException e) {
            logger.error("Error not properly handled.", e);
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<>(delegate) {

            @Override
            public void onHalfClose() {
                try {
                    super.onHalfClose();
                } catch (RuntimeException e) {
                    logger.error("Error not properly handled.", e);
                    call.close(Status.INTERNAL
                                    .withCause(e)
                                    .withDescription(e.getMessage()),
                            new Metadata());
                }
            }

            @Override
            public void onReady() {
                try {
                    super.onReady();
                } catch (RuntimeException e) {
                    logger.error("Error", e);
                    throw e;
                }
            }
        };
    }
}
