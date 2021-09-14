package com.oognuyh.kanban.web;

import com.oognuyh.kanban.model.Board;
import com.oognuyh.kanban.service.BoardService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BoardHandler {
    private final BoardService boardService;

    public Mono<ServerResponse> findAll(ServerRequest request) {
        return ServerResponse.ok().body(boardService.findAll(), Board.class);
    }

    public Mono<ServerResponse> findByUserId(ServerRequest request) {
        Flux<Board> boards = ReactiveSecurityContextHolder.getContext()
            .map(context -> context.getAuthentication().getName())
            .flatMapMany(boardService::findByUserId);

        return ServerResponse.ok().body(boards, Board.class);            
    }

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");

        return boardService.findById(id)
            .flatMap(board -> ServerResponse.ok().bodyValue(board))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createNew(ServerRequest request) {
        return ReactiveSecurityContextHolder.getContext()
            .map(context -> context.getAuthentication().getName()).log()
            .flatMap(boardService::createNew)
            .flatMap(board -> ServerResponse.ok().bodyValue(board))
            .switchIfEmpty(ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()).log();
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        return request.bodyToMono(Board.class).log()
            .flatMap(boardService::saveOrUpdate)
            .flatMap(board -> ServerResponse.ok().bodyValue(board))
            .switchIfEmpty(ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> deleteById(ServerRequest request) {
        String id = request.pathVariable("id");

        return ServerResponse.ok()
            .build(boardService.deleteById(id));
    }
}
