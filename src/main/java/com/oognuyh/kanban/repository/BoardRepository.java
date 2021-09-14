package com.oognuyh.kanban.repository;

import com.oognuyh.kanban.model.Board;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

@Repository
public interface BoardRepository extends ReactiveMongoRepository<Board, String> {
    
    Flux<Board> findByUserId(String userId);
}