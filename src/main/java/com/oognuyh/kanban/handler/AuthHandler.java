package com.oognuyh.kanban.handler;

import com.oognuyh.kanban.security.JwtUtils;
import com.oognuyh.kanban.security.Tokens;
import com.oognuyh.kanban.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final UserService userService;

    public Mono<ServerResponse> refreshTokens(ServerRequest request) {
        return request.bodyToMono(Tokens.class)
            .flatMap(tokens -> userService.refreshTokens(tokens.getRefreshToken()))
            .flatMap(tokens -> ServerResponse.ok()
                .header(JwtUtils.AUTH_TOKEN_HEADER, tokens.getAuthToken())
                .header(JwtUtils.REFRESH_TOKEN_HEADER, tokens.getRefreshToken())
                .build())
            .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .map(context -> (String) context.getAuthentication().getPrincipal())
            .flatMap(id -> userService.findById(id)).log()
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .switchIfEmpty(ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }   
}
