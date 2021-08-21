package com.oognuyh.kanban.config;

import com.oognuyh.kanban.security.ServerAuthenticationSuccessHandlerImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ServerAuthenticationSuccessHandlerImpl serverAuthenticationSuccessHandler;

    @Bean
    SecurityWebFilterChain springSecurityWebFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange()
            .pathMatchers("/oauth2/**", "/login/**", "/", "/js/**", "/css/**", "/favicon.ico", "/img/**")
            .permitAll()
            .anyExchange()
            .authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((exchange, exception) -> Mono.error(exception))
            .accessDeniedHandler((exchange, exception) -> Mono.error(exception))
            .and()
            .oauth2Login()
            .authenticationSuccessHandler(serverAuthenticationSuccessHandler)
            .and()
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable();
            
        return http.build();
    }
}