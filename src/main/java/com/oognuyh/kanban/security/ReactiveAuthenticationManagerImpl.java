package com.oognuyh.kanban.security;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class ReactiveAuthenticationManagerImpl implements ReactiveAuthenticationManager {

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication.getCredentials().toString())
            .map(JwtUtils::verify)
            .onErrorReturn(VerifiedResult.ERROR())
            .filter(verifiedResult -> verifiedResult.isSuccessful())
            .switchIfEmpty(Mono.empty())
            .map(verifiedResult -> new UsernamePasswordAuthenticationToken(verifiedResult.getId(), null, verifiedResult.getAuthorities()));
    }
}
