package com.oognuyh.kanban.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifiedResult {
    
    private String id;

    private boolean isSuccessful;

    private Collection<GrantedAuthority> authorities;

    public static VerifiedResult OK(String id, String[] authorities) {
        return VerifiedResult.builder()
            .isSuccessful(true)
            .id(id)
            .authorities(Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }

    public static VerifiedResult ERROR(String id, String[] authorities) {
        return VerifiedResult.builder()
            .isSuccessful(false)
            .id(id)
            .authorities(Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()))
            .build();
    }

    public static VerifiedResult ERROR() {
        return VerifiedResult.builder()
            .isSuccessful(false)
            .build();
    }
}
