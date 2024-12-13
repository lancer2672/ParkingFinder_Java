package com.project.parkingfinder.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class IOConfig implements WebSocketConfigurer {

    private final IOController mEngineIoHandler;

    public IOConfig(IOController engineIoHandler) {
        mEngineIoHandler = engineIoHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(mEngineIoHandler, "/socket.io/")
                .addInterceptors(mEngineIoHandler)
                .setAllowedOrigins("*");
    }
}
