package com.oognuyh.kanban.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.security.Role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import reactor.test.StepVerifier;

@DataMongoTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @DisplayName("save a user")
    @Test
    void save() {
        // given
        User user = User.builder()
            .email("email")
            .imageUrl("imageUrl")
            .roles(Arrays.stream(new Role[] {Role.USER}).collect(Collectors.toList()))
            .build();

        // when
        repository.save(user)
            .as(StepVerifier::create)
            // then
            .expectNextMatches(savedUser -> {
                assertThat(savedUser.getId()).isNotNull();
                assertThat(savedUser.getEmail()).isEqualTo(user.getEmail());
                return true;
            })
            .verifyComplete();
    }

    @DisplayName("find a user by email and provider")
    @Test
    void findByEmailAndProvider() {
        // given
        User user = User.builder().email("email").provider("provider").build();

        // when
        repository.save(user)
            .flatMap(savedUser -> repository.findByEmailAndProvider("email", "provider"))
            // then
            .as(StepVerifier::create)
            .assertNext(savedUser -> assertAll(
                () -> assertThat(savedUser.getEmail()).isEqualTo(user.getEmail()),
                () -> assertThat(savedUser.getProvider()).isEqualTo(user.getProvider())
            ))
            .verifyComplete();
    }

    @DisplayName("find a user by id")
    @Test
    void findById() {
        // when
        User user = User.builder().email("email").build();

        // given
        repository.save(user)
            .map(User::getId)
            .flatMap(repository::findById)
            .as(StepVerifier::create)
            // then
            .expectNextCount(1)
            .verifyComplete();
    }
}