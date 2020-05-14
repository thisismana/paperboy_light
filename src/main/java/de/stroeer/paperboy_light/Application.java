package de.stroeer.paperboy_light;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.stroeer.paperboy_light.di.PaperboyModule;
import de.stroeer.paperboy_light.services.GrpcServer;
import de.stroeer.paperboy_light.services.MyHttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Application {

    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);


    public static void main(String[] args) throws IOException, InterruptedException {

        Injector injector = Guice.createInjector(new PaperboyModule());
        GrpcServer grpc_server = injector.getInstance(GrpcServer.class);
        MyHttpServer http_server = injector.getInstance(MyHttpServer.class);


        grpc_server.start();
        http_server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Use stderr here since the logger may have been reset by its JVM shutdown hook.
            System.err.println("*** shutting down gRPC and HTTP server since JVM is shutting down");
            http_server.shutdown();
            try {
                grpc_server.shutdown();
            } catch (InterruptedException e) {
                logger.error("Could not shutdown");
                e.printStackTrace(System.err);
            }

            System.err.println("*** server shut down");
        }));

        grpc_server.block_until_shutdown();
    }


}
