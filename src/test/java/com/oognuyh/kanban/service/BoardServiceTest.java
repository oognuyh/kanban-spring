package com.oognuyh.kanban.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;

import com.oognuyh.kanban.model.Board;
import com.oognuyh.kanban.model.Task;
import com.oognuyh.kanban.repository.BoardRepository;
import com.oognuyh.kanban.service.impl.BoardServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)
public class BoardServiceTest {
    @InjectMocks
    private BoardServiceImpl service;

    @Mock
    private BoardRepository repository;

    @DisplayName("save or update a board")
    @Test
    void saveOrUpdate() {
        // given
        given(repository.save(any(Board.class)))
            .willReturn(Mono.just(Board.init("someone")));

        // when
        service.saveOrUpdate(Board.init("someone"))
            .flatMap(savedBoard -> {
                savedBoard.setTitle("updated title");   
                return service.saveOrUpdate(savedBoard);
            })
            .as(StepVerifier::create)
            // then
            .expectNextMatches(updatedBoard -> {
                assertThat(updatedBoard.getTitle()).isEqualTo("updated title");
                return true;
            })
            .verifyComplete();
    }

    @DisplayName("create new board")
    @Test
    void createNewBoard() {
        // given
        given(repository.save(any(Board.class)))
            .willReturn(Mono.just(Board.init("someone")));

        // when
        service.createNew("someone")
            .as(StepVerifier::create)
            // then
            .expectNextMatches(newBoard -> {
                assertThat(newBoard.getTitle()).isEqualTo("New Board");
                assertThat(newBoard.getColumns().size()).isEqualTo(4);
                assertThat(newBoard.getTags().size()).isEqualTo(4);
                return true;
            })
            .verifyComplete();
    }

    @DisplayName("find boards by user id")
    @Test
    void findBoardsByUserId() {
        // given
        given(repository.findByUserId(anyString()))
            .willReturn(Flux.fromIterable(Arrays.asList(Board.init("someone"), Board.init("someone"))));

        // when
        service.findByUserId("someone")
            .as(StepVerifier::create)
            // then
            .expectNextMatches(board -> {
                assertThat(board.getUserId()).isEqualTo("someone");
                return true;
            })
            .expectNextMatches(board -> {
                assertThat(board.getUserId()).isEqualTo("someone");
                return true;
            })
            .verifyComplete();
    }

    @DisplayName("find a board by id")
    @Test
    void findBoardById() {
        // given
        given(repository.findById(anyString()))
            .willReturn(Mono.just(Board.builder().id("id").build()));

        // when
        service.findById("id")
            .as(StepVerifier::create)
            // then
            .expectNextCount(1)
            .verifyComplete();
    }

    @DisplayName("delete a board by id")
    @Test
    void deleteBoardById() {
        // given
        given(repository.deleteById("id"))
            .willReturn(Mono.empty());

        // when
        service.deleteById("id")
            .as(StepVerifier::create)
            // then
            .verifyComplete();
    }

    @DisplayName("find tasks by user id")
    void findTasksByUserId() {
        // given
        Board board = Board.init("someone");
        board.getColumns().get(0).getTasks().add(Task.builder().build());
        board.getColumns().get(1).getTasks().add(Task.builder().build());
        board.getColumns().get(2).getTasks().add(Task.builder().build());

        given(repository.findByUserId(anyString()))
            .willReturn(Flux.fromIterable(Arrays.asList(board)));

        // when
        service.findTasksByUserId("someone")
            .as(StepVerifier::create)
            // then
            .expectNextCount(3)
            .verifyComplete();
    }
}
