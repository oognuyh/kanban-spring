package com.oognuyh.kanban.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import com.oognuyh.kanban.model.Board;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import reactor.test.StepVerifier;

@DataMongoTest
public class BoardRepositoryTest {
    @Autowired
    private BoardRepository repository;

    @DisplayName("save initial board")
    @Test
    void save() {
        // given
        Board board = Board.init("someone");

        // when
        repository.save(board)
            .as(StepVerifier::create)
            .expectNextMatches(savedBoard -> {
                // then
                assertThat(savedBoard.getId()).isNotNull();
                assertThat(savedBoard.getTitle()).isEqualTo(board.getTitle());
                assertThat(savedBoard.getColumns().size()).isEqualTo(board.getColumns().size());
                assertThat(savedBoard.getUserId()).isEqualTo(board.getUserId());
                assertThat(savedBoard.getTags().size()).isEqualTo(board.getTags().size());

                return true;
            })
            .verifyComplete();
    }

    @DisplayName("find boards by user id")
    @Test
    void findBoardsByUserId() {
        // given
        repository.saveAll(Arrays.asList(Board.init("target"), Board.init("another"))).subscribe();

        // when
        repository.findByUserId("target")
            .as(StepVerifier::create)
            // then
            .expectNextCount(1)
            .verifyComplete();
    }

    @DisplayName("find board by id")
    @Test
    void findBoardById() {
        // given
        Board board = Board.init("someone");

        // when
        repository.save(board)
            .map(Board::getId)
            .flatMap(repository::findById)
            .as(StepVerifier::create)
            // then
            .expectNext(board)
            .verifyComplete();
    }
}
