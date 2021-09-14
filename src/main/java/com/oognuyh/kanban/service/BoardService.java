package com.oognuyh.kanban.service;

import com.oognuyh.kanban.model.Board;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BoardService {
    
    Flux<Board> findAll();
    Flux<Board> findByUserId(String userId);
    Mono<Board> findById(String id);
    Mono<Board> saveOrUpdate(Board board);
    Mono<Void> deleteById(String id);
    Mono<Board> createNew(String userId);
}
