package de.stroeer.paperboy_light.services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Singleton
public class MyHttpServer {

    private static final Logger logger = LoggerFactory.getLogger(MyHttpServer.class);
    private final static ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    private final HttpServer server;
    private final int port;

    @Inject
    public MyHttpServer(@Named("http_port") int port) throws IOException {
        this.port = port;
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void start() {
        logger.info("HTTP Server started, listening on " + port);
        server.createContext("/", new HealthProbeHandler());
        server.setExecutor(threadPoolExecutor);
        server.start();
    }

    public void shutdown() {
        server.stop(0);
    }

    public static final class HealthProbeHandler implements HttpHandler {
        private static final Logger logger = LoggerFactory.getLogger(MyHttpServer.class);
        private static final String ok_response = "{\"status\":\"ok\"}";

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.info("HTTP: {}", exchange.getRequestURI().toString());
            if (Set.of("GET", "HEAD").contains(exchange.getRequestMethod().toUpperCase())) {
                OutputStream out = exchange.getResponseBody();
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
                exchange.sendResponseHeaders(200, ok_response.length());
                out.write(ok_response.getBytes(StandardCharsets.UTF_8));
                out.flush();
                out.close();

            } else {
                exchange.sendResponseHeaders(405, 0);
            }
            exchange.close();
        }
    }


}
