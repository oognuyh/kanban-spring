package com.oognuyh.kanban.security;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;

import reactor.core.publisher.Mono;

/*
    Add JWT after a successful login
 */
@Component
public class ServerAuthenticationSuccessHandlerImpl implements ServerAuthenticationSuccessHandler {
    @Value("${client.redirectUri}")
    private String redirectUri;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerWebExchange exchange = webFilterExchange.getExchange();
        HttpHeaders headers = exchange.getResponse().getHeaders();
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String email = oAuth2AuthenticationToken.getPrincipal().getAttributes().get("email").toString();
        String[] authorities = oAuth2AuthenticationToken.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .toArray(String[]::new);

        String authToken = JwtUtils.generateAuthToken(email, authorities);
        String refreshToken = JwtUtils.generateRefreshToken(email, authorities);
        
        exchange.getResponse().setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        try {
            URI uri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("authToken", authToken)
                .queryParam("refreshToken", refreshToken)
                .build()
                .toUri();

            headers.setLocation(uri);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        
        return exchange.getResponse().setComplete();
    }
} 
