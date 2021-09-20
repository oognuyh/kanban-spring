package com.oognuyh.kanban.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.repository.UserRepository;
import com.oognuyh.kanban.security.JwtUtils;
import com.oognuyh.kanban.security.Role;
import com.oognuyh.kanban.service.impl.UserServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserServiceImpl service;

    @Mock
    private UserRepository repository;

    @DisplayName("save or update a user")
    @Test
    void saveOrUpdate() {
        // given
        User user = User.builder()
            .name("name")
            .email("email")
            .imageUrl("imageUrl")
            .provider("provider")
            .build();

        given(repository.findByEmailAndProvider(anyString(), anyString()))
            .willReturn(Mono.empty());
        given(repository.save(any(User.class)))
            .willReturn(Mono.just(user));

        // when
        service.saveOrUpdate(user)
            .as(StepVerifier::create)
            // then
            .expectNextMatches(savedUser -> {
                assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
                assertThat(savedUser.getName()).isEqualTo(user.getName());
                assertThat(savedUser.getImageUrl()).isEqualTo(user.getImageUrl());
                return true;
            })
            .verifyComplete();            
    }

    @DisplayName("find a user by id")
    @Test
    void findById() {
        // given
        given(repository.findById(anyString()))
            .willReturn(Mono.just(User.builder()
                    .id("id")
                    .build()));

        // when
        service.findById("id")
            .as(StepVerifier::create)
            // then
            .expectNextCount(1)
            .verifyComplete();            
    }

    @DisplayName("refresh tokens")
    @Test
    void refreshTokens() {
        // given
        User user = User.builder()
            .id("id")
            .email("email")
            .imageUrl("imageUrl")
            .roles(Arrays.stream(new Role[] {Role.USER}).collect(Collectors.toList()))
            .build();

        String refreshToken = JwtUtils.generateRefreshToken(user.getId(), user.getRoles().stream()
            .map(role -> role.name())
            .toArray(String[]::new));

        given(repository.findById(anyString()))
            .willReturn(Mono.just(user));

        // when
        service.refreshTokens(refreshToken)
            .as(StepVerifier::create)
            // then
            .expectNextMatches(tokens -> {
                assertThat(tokens).isNotNull();
                assertThat(JwtUtils.verify(tokens.getAuthToken()).getId()).isEqualTo(user.getId());
                return true;
            })
            .verifyComplete();
    }
}
