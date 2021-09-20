package com.oognuyh.kanban.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.oognuyh.kanban.model.Board;
import com.oognuyh.kanban.model.Tag;
import com.oognuyh.kanban.model.Task;
import com.oognuyh.kanban.model.User;
import com.oognuyh.kanban.security.ReactiveAuthenticationManagerImpl;
import com.oognuyh.kanban.security.Role;
import com.oognuyh.kanban.service.BoardService;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
public class BoardHandlerTest {
    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BoardService boardService;
    
    @MockBean
    private ReactiveAuthenticationManagerImpl authenticationManager;

    private User user;
    
    @BeforeEach
    void setup() {
        user = User.builder()
            .id("user id")
            .name("name")
            .email("email")
            .imageUrl("image URL")
            .roles(Arrays.asList(Role.USER))
            .build();

        given(authenticationManager.authenticate(any()))
            .willReturn(Mono.just(new UsernamePasswordAuthenticationToken(user.getId(), null, user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList()))));
    }

    @DisplayName("create a new board")
    @Test
    void createNew(){
        // given
        Board board = Board.init(user.getId());
        board.setId("id");

        given(boardService.createNew(anyString()))
            .willReturn(Mono.just(board));

        // when
        webTestClient.post()
            .uri("/api/v1/board")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.userId").isEqualTo(user.getId())
            .jsonPath("$.title").isEqualTo("New Board")
            .jsonPath("$.tags.length()").isEqualTo(4)
            .jsonPath("$.columns.length()").isEqualTo(4)
            .jsonPath("$.columns[0].title").isEqualTo("Backlog")
            .jsonPath("$.columns[1].title").isEqualTo("To do")
            .jsonPath("$.columns[2].title").isEqualTo("In progress")
            .jsonPath("$.columns[3].title").isEqualTo("Done")
            .consumeWith(document("create-new-board",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE)
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록"),
                    fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색"),
                    fieldWithPath("columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록"),
                    fieldWithPath("columns[].title").type(JsonFieldType.STRING).description("행의 제목"),
                    fieldWithPath("columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록")
                )
            ));
    }

    @DisplayName("update a board")
    @Test
    void update() {
        // given
        Task newTask = Task.builder()
            .userName("userName")
            .userImageUrl("userImageUrl")
            .content("content")
            .tags(Arrays.asList(Tag.builder().name("name").color("#5F8079").textColor("white").build()))
            .createdAt(LocalDateTime.now())
            .deadline(LocalDate.now())
            .build();

        Board board = Board.init(user.getId());
        board.setId("id");
        board.getColumns().get(0).getTasks().add(newTask);

        given(boardService.saveOrUpdate(any(Board.class)))
            .willReturn(Mono.just(board));

        // when
        webTestClient
            .put()
            .uri("/api/v1/board")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .body(BodyInserters.fromValue(board))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.columns[0].tasks").isNotEmpty()
            .consumeWith(document("update-board",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                requestFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록").optional(),
                    fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그의 이름").optional(),
                    fieldWithPath("tags[].color").type(JsonFieldType.STRING).description("태그의 배경색").optional(),
                    fieldWithPath("tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색").optional(),
                    fieldWithPath("columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록").optional(),
                    fieldWithPath("columns[].title").type(JsonFieldType.STRING).description("행의 제목").optional(),
                    fieldWithPath("columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록").optional(),
                    fieldWithPath("columns[].tasks[].content").type(JsonFieldType.STRING).description("일의 내용").optional(),
                    fieldWithPath("columns[].tasks[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름").optional(),
                    fieldWithPath("columns[].tasks[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지").optional(),
                    fieldWithPath("columns[].tasks[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자").optional(),
                    fieldWithPath("columns[].tasks[].deadline").type(JsonFieldType.STRING).description("일의 마감일자").optional(),
                    fieldWithPath("columns[].tasks[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("columns[].tasks[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름").optional(),
                    fieldWithPath("columns[].tasks[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색").optional(),
                    fieldWithPath("columns[].tasks[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색").optional()
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE)
                ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록").optional(),
                    fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색"),
                    fieldWithPath("columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록"),
                    fieldWithPath("columns[].title").type(JsonFieldType.STRING).description("행의 제목"),
                    fieldWithPath("columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록").optional(),
                    fieldWithPath("columns[].tasks[].content").type(JsonFieldType.STRING).description("일의 내용"),
                    fieldWithPath("columns[].tasks[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름"),
                    fieldWithPath("columns[].tasks[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지"),
                    fieldWithPath("columns[].tasks[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자"),
                    fieldWithPath("columns[].tasks[].deadline").type(JsonFieldType.STRING).description("일의 마감일자"),
                    fieldWithPath("columns[].tasks[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("columns[].tasks[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("columns[].tasks[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("columns[].tasks[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색")
                )
            ));
    }

    @DisplayName("update invalid board")
    @Test
    void updateInvalidBoard() {
        // given
        Board board = Board.builder()
            .id("id")
            .title("greater than 20 characters")
            .userId("userId")
            .build();

        // when
        webTestClient
            .put()
            .uri("/api/v1/board")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .body(BodyInserters.fromValue(board))
            .exchange()
            // then
            .expectStatus()
            .isBadRequest()
            .expectBody()
            .consumeWith(document("update-invalid-board",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                requestFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록").optional(),
                    fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그의 이름").optional(),
                    fieldWithPath("tags[].color").type(JsonFieldType.STRING).description("태그의 배경색").optional(),
                    fieldWithPath("tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색").optional(),
                    fieldWithPath("columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록").optional(),
                    fieldWithPath("columns[].title").type(JsonFieldType.STRING).description("행의 제목").optional(),
                    fieldWithPath("columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록").optional(),
                    fieldWithPath("columns[].tasks[].content").type(JsonFieldType.STRING).description("일의 내용").optional(),
                    fieldWithPath("columns[].tasks[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름").optional(),
                    fieldWithPath("columns[].tasks[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지").optional(),
                    fieldWithPath("columns[].tasks[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자").optional(),
                    fieldWithPath("columns[].tasks[].deadline").type(JsonFieldType.STRING).description("일의 마감일자").optional(),
                    fieldWithPath("columns[].tasks[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("columns[].tasks[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름").optional(),
                    fieldWithPath("columns[].tasks[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색").optional(),
                    fieldWithPath("columns[].tasks[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색").optional()
                ),
                responseHeaders(
                    headerWithName(HttpHeaders.CONTENT_TYPE).description(MediaType.APPLICATION_JSON_VALUE)
                ),
                relaxedResponseFields(
                    fieldWithPath("timestamp").description("요청 시간"),
                    fieldWithPath("path").description("요청 경로"),
                    fieldWithPath("status").description("응답 상태"),
                    fieldWithPath("error").description("오류명"),
                    fieldWithPath("message").description("오류 메시지"),
                    fieldWithPath("requestId").description("요청 고유번호"),
                    fieldWithPath("{요청 필드}").description("{해당 필드의 제약조건}").type(JsonFieldType.STRING).optional()
                )
            )); 
    }

    @DisplayName("find a board by id")
    @Test
    void findById() {
        // given
        Task newTask = Task.builder()
            .userName("userName")
            .userImageUrl("userImageUrl")
            .content("content")
            .tags(Arrays.asList(Tag.builder().name("name").color("#5F8079").textColor("white").build()))
            .createdAt(LocalDateTime.now())
            .deadline(LocalDate.now())
            .build();

        Board board = Board.init(user.getId());
        board.setId("id");
        board.getColumns().get(0).getTasks().add(newTask);

        given(boardService.findById(anyString()))
            .willReturn(Mono.just(board));

        // when
        webTestClient
            .get()
            .uri("/api/v1/board/{id}", board.getId())
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(board.getId())
            .consumeWith(document("find-by-id", 
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                pathParameters(
                    parameterWithName("id").description("검색할 보드의 고유번호")
	            ),
                responseFields(
                    fieldWithPath("id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록").optional(),
                    fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색"),
                    fieldWithPath("columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록"),
                    fieldWithPath("columns[].title").type(JsonFieldType.STRING).description("행의 제목"),
                    fieldWithPath("columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록").optional(),
                    fieldWithPath("columns[].tasks[].content").type(JsonFieldType.STRING).description("일의 내용"),
                    fieldWithPath("columns[].tasks[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름"),
                    fieldWithPath("columns[].tasks[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지"),
                    fieldWithPath("columns[].tasks[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자"),
                    fieldWithPath("columns[].tasks[].deadline").type(JsonFieldType.STRING).description("일의 마감일자"),
                    fieldWithPath("columns[].tasks[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("columns[].tasks[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("columns[].tasks[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("columns[].tasks[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색")
                )
            ));
            
            // then
    }

    @DisplayName("delete a board by id")
    @Test
    void deleteById() {
        // given
        given(boardService.deleteById(anyString()))
            .willReturn(Mono.empty());

        // when
        webTestClient
            .delete()
            .uri("/api/v1/board/{id}", "id")
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .consumeWith(document("delete-by-id", 
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("삭제할 보드의 고유번호")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                )
            ));
    }

    @DisplayName("find user's tasks")
    @Test
    void findTasksByUserId() {
        // given
        List<Task> tasks = Arrays.asList(Task.builder()
            .userName("userName")
            .userImageUrl("userImageUrl")
            .content("content")
            .tags(Arrays.asList(Tag.builder().name("name").color("#5F8079").textColor("white").build()))
            .createdAt(LocalDateTime.now())
            .deadline(LocalDate.now())
            .build());

        given(boardService.findTasksByUserId(anyString()))
            .willReturn(Flux.fromIterable(tasks));

        // when
        webTestClient
            .get()
            .uri("/api/v1/board/tasks")
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .consumeWith(document("find-tasks-by-user-id", 
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                responseFields(
                    fieldWithPath("[].content").type(JsonFieldType.STRING).description("일의 내용"),
                    fieldWithPath("[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름"),
                    fieldWithPath("[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지"),
                    fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자"),
                    fieldWithPath("[].deadline").type(JsonFieldType.STRING).description("일의 마감일자"),
                    fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색")
                )
            ));
    }

    @DisplayName("find user's boards")
    @Test
    void findByUserId() {
        // given
        Task newTask = Task.builder()
            .userName("userName")
            .userImageUrl("userImageUrl")
            .content("content")
            .tags(Arrays.asList(Tag.builder().name("name").color("#5F8079").textColor("white").build()))
            .createdAt(LocalDateTime.now())
            .deadline(LocalDate.now())
            .build();
            
        Board board = Board.init(user.getId());
        board.setId("id");
        board.getColumns().get(0).getTasks().add(newTask);

        given(boardService.findByUserId(user.getId()))
            .willReturn(Flux.fromIterable(Arrays.asList(board)));

        // when
        webTestClient
            .get()
            .uri("/api/v1/board/mine")
            .accept(MediaType.APPLICATION_JSON)
            .headers(header -> header.setBearerAuth("YOUR TOKEN"))
            .exchange()
            // then
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .consumeWith(document("find-boards-by-user-id",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description(MediaType.APPLICATION_JSON_VALUE),
                    headerWithName(HttpHeaders.AUTHORIZATION).description("부여받은 인증 토큰")
                ),
                responseFields(
                    fieldWithPath("[].id").type(JsonFieldType.STRING).description("보드의 고유번호"),
                    fieldWithPath("[].title").type(JsonFieldType.STRING).description("보드의 제목"),
                    fieldWithPath("[].userId").type(JsonFieldType.STRING).description("보드의 사용자 이름"),
                    fieldWithPath("[].tags").type(JsonFieldType.ARRAY).description("보드의 태그 목록").optional(),
                    fieldWithPath("[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색"),
                    fieldWithPath("[].columns").type(JsonFieldType.ARRAY).description("현재 보드의 행 목록"),
                    fieldWithPath("[].columns[].title").type(JsonFieldType.STRING).description("행의 제목"),
                    fieldWithPath("[].columns[].tasks").type(JsonFieldType.ARRAY).description("행의 일 목록").optional(),
                    fieldWithPath("[].columns[].tasks[].content").type(JsonFieldType.STRING).description("일의 내용"),
                    fieldWithPath("[].columns[].tasks[].userName").type(JsonFieldType.STRING).description("일의 등록자 이름"),
                    fieldWithPath("[].columns[].tasks[].userImageUrl").type(JsonFieldType.STRING).description("일의 등록자 이미지"),
                    fieldWithPath("[].columns[].tasks[].createdAt").type(JsonFieldType.STRING).description("일의 생성일자"),
                    fieldWithPath("[].columns[].tasks[].deadline").type(JsonFieldType.STRING).description("일의 마감일자"),
                    fieldWithPath("[].columns[].tasks[].tags").type(JsonFieldType.ARRAY).description("일의 등록된 태그 목록").optional(),
                    fieldWithPath("[].columns[].tasks[].tags[].name").type(JsonFieldType.STRING).description("태그의 이름"),
                    fieldWithPath("[].columns[].tasks[].tags[].color").type(JsonFieldType.STRING).description("태그의 배경색"),
                    fieldWithPath("[].columns[].tasks[].tags[].textColor").type(JsonFieldType.STRING).description("태그의 글자색")
                )
            ));
    }
}
