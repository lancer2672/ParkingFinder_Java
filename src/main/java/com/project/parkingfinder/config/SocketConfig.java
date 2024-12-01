package com.project.parkingfinder.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@Slf4j
@org.springframework.context.annotation.Configuration
public class SocketConfig {

//    @Value("${socket.host:localhost}")
    private String host = "localhost";

    @Value("${socket.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer() {
        // Configure Socket.IO server
        Configuration config = new Configuration();
        config.setHostname(host);
        config.setPort(port);

        // Allow Cross-Origin Resource Sharing (CORS)
        config.setOrigin("*");

        SocketIOServer server = new SocketIOServer(config);

        // Handle client connection
        server.addConnectListener(client -> {
            log.info("New client connected: {}", client.getSessionId());
        });

        // Handle client disconnection
        server.addDisconnectListener(client -> {
            log.info("Client disconnected: {}", client.getSessionId());
        });

        return server;
    }

    @Bean
    public CommandLineRunner socketIOServerRunner(SocketIOServer server) {
        return args -> {
            // Start the Socket.IO server
            server.start();
            log.info("Socket.IO server started on {}:{}", host, port);

            // Add a shutdown hook to stop the server gracefully
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                server.stop();
                log.info("Socket.IO server stopped.");
            }));
        };
    }
}
