package com.oognuyh.kanban.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.security.JwtUtils;
import com.oognuyh.kanban.security.ReactiveAuthenticationManagerImpl;
import com.oognuyh.kanban.security.Role;
import com.oognuyh.kanban.security.Tokens;
import com.oognuyh.kanban.service.UserService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Mono;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public class AuthHandlerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @MockBean
    private ReactiveAuthenticationManagerImpl authenticationManager;
    
    @DisplayName("get userinfo with authorized token")
    @Test
    void getUser() {
        // given
        User user = User.builder()
            .id("id")
            .name("name")
            .email("email")
            .imageUrl("image URL")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now().plusMinutes(30))
            .roles(Arrays.asList(Role.USER))
            .build();

        given(userService.findById(anyString()))
            .willReturn(Mono.just(user));

        given(authenticationManager.authenticate(any()))
            .willReturn(Mono.just(new UsernamePasswordAuthenticationToken(user.getId(), null, user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList()))));

        // when
        webTestClient
            .get()
            .uri("/api/v1/auth/user")
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(document("get-user", 
                requestHeaders(
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE)
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("사용자 고유번호"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름"),
                    fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                    fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("사용자 이미지 주소"),
                    fieldWithPath("createdAt").type(JsonFieldType.STRING).description("사용자 가입일자"),
                    fieldWithPath("updatedAt").type(JsonFieldType.STRING).description("사용자 수정일자")
                )
            ));
    }

    @DisplayName("get userinfo without token")
    @Test
    void getUserWithoutToken() {
        // given
        given(authenticationManager.authenticate(any()))
            .willReturn(Mono.empty());

        // when
        webTestClient
            .get()
            .uri("/api/v1/auth/user")
            .headers(header -> header.setBearerAuth("EMPTY OR WRONG TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isUnauthorized();
    }

    @DisplayName("refresh tokens")
    @Test
    void refreshTokens() {
        // given
        given(userService.refreshTokens(anyString()))
            .willReturn(Mono.just(Tokens.builder().authToken("NEW TOKEN").refreshToken("NEW TOKEN").build()));

        // when
        webTestClient
            .post()
            .uri("/api/v1/auth/refreshTokens")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(Collections.singletonMap("refreshToken", "EXISTING REFRESH TOKEN")))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(document("refresh-tokens", 
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE)
                ),
                requestFields(
                    fieldWithPath("refreshToken").description("기존 리프레시 토큰")
                ),
                responseHeaders(
                    headerWithName(JwtUtils.AUTH_TOKEN_HEADER).description("갱신된 인증 토큰"),
                    headerWithName(JwtUtils.REFRESH_TOKEN_HEADER).description("갱신된 리프레시 토큰")
                )
            ));

    }
}
