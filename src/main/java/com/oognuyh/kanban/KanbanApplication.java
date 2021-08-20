package com.oognuyh.kanban;

import com.oognuyh.kanban.model.Board;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import lombok.RequiredArgsConstructor;

@SpringBootApplication
@RequiredArgsConstructor
@EnableReactiveMongoAuditing
public class KanbanApplication {
	private final ReactiveMongoTemplate template;

	public static void main(String[] args) {
		SpringApplication.run(KanbanApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase() {
		return (args) -> {
			template.createCollection(Board.class, CollectionOptions.empty().capped().size(1000000).maxDocuments(1000)).subscribe();

			template.insert(Board.builder().title("board").build()).subscribe();
		};
	}
}
