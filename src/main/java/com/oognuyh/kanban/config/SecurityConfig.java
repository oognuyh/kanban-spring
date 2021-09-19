package com.oognuyh.kanban.config;

import com.oognuyh.kanban.security.ServerAuthenticationSuccessHandlerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ServerAuthenticationSuccessHandlerImpl serverAuthenticationSuccessHandler;
    private final ServerSecurityContextRepository serverSecurityContextRepository;
    private final ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Bean
    SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                .pathMatchers("/oauth2/**", "/api/v1/auth/refreshTokens")
                .permitAll()
                .anyExchange()
                .authenticated()
            .and()
            .securityContextRepository(serverSecurityContextRepository)
            .authenticationManager(reactiveAuthenticationManager)
            .exceptionHandling()
                .authenticationEntryPoint((exchange, exception) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((exchange, exception) -> Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
            .and()
            .oauth2Login()
                .authenticationSuccessHandler(serverAuthenticationSuccessHandler)
            .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
            .build(); 
    }
}