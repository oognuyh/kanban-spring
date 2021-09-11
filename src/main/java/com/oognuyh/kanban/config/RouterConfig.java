package com.oognuyh.kanban.config;

import com.oognuyh.kanban.web.AuthHandler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {
    private final AuthHandler authHandler;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return RouterFunctions
            .nest(RequestPredicates.path("/api/v1/auth"), RouterFunctions.route()
                .POST("/refreshTokens", authHandler::refreshTokens)
                .GET("/user", authHandler::getUser)
                .build());
    }
}
