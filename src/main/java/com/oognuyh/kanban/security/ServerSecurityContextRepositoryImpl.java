package com.oognuyh.kanban.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ServerSecurityContextRepositoryImpl implements ServerSecurityContextRepository {
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
            .filter(authToken -> authToken.startsWith(JwtUtils.PREFIX))
            .map(authToken -> authToken.substring(JwtUtils.PREFIX.length()))
            .map(authToken -> new UsernamePasswordAuthenticationToken(authToken, authToken))
            .flatMap(authentication -> reactiveAuthenticationManager.authenticate(authentication))
            .map(SecurityContextImpl::new);
    }
}
