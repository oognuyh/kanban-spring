package com.oognuyh.kanban.config;

import com.oognuyh.kanban.handler.AuthHandler;
import com.oognuyh.kanban.handler.BoardHandler;

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
    private final BoardHandler boardHandler;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return RouterFunctions
            .nest(RequestPredicates.path("/api/v1/auth"), RouterFunctions.route()
                .POST("/refreshTokens", authHandler::refreshTokens)
                .GET("/user", authHandler::getUser)
                .build());
    }

    @Bean
    public RouterFunction<ServerResponse> boardRoutes() {
        return RouterFunctions
            .nest(RequestPredicates.path("/api/v1/board"), RouterFunctions.route()
                .GET("/mine", boardHandler::findByUserId)
                .GET("/tasks", boardHandler::findTasksByUserId)
                .GET("/{id}", boardHandler::findById)
                .PUT("", boardHandler::update)
                .POST("", boardHandler::createNew)
                .DELETE("/{id}", boardHandler::deleteById)
                .build());
    }
}
