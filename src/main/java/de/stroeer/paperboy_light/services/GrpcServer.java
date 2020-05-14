package de.stroeer.paperboy_light.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.stroeer.paperboy_light.interceptors.ExceptionInterceptor;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Singleton
public class GrpcServer {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);

    private final int port;
    private final Server server;

    @Inject
    public GrpcServer(@Named("grpc_port") int port, ArticleService as) {
        this.port = port;
        logger.info("Set up ArticleService.");

        server = ServerBuilder.forPort(port)
                .addService(as)
                .intercept(new ExceptionInterceptor())
                .build();
    }


    public void start() throws IOException, InterruptedException {
        server.start();
        logger.info("GRPC Server started, listening on " + port);
    }

    public void block_until_shutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void shutdown() throws InterruptedException {
        server.shutdown()
                .awaitTermination(30, TimeUnit.SECONDS);

    }
}
