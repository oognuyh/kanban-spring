package com.oognuyh.kanban.service.impl;

import com.oognuyh.kanban.model.Board;
import com.oognuyh.kanban.repository.BoardRepository;
import com.oognuyh.kanban.service.BoardService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("boardService")
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;

    public Flux<Board> findAll() {
        return boardRepository.findAll();
    }

    @Override
    public Flux<Board> findByUserId(String userId) {
        return boardRepository.findByUserId(userId);
    }

    @Override
    public Mono<Board> findById(String id) {
        return boardRepository.findById(id);
    }

    @Override
    public Mono<Board> saveOrUpdate(Board board) {
        return boardRepository.save(board);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return boardRepository.deleteById(id);
    }

    @Override
    public Mono<Board> createNew(String userId) {
        return boardRepository.save(Board.init(userId));
    }
}
