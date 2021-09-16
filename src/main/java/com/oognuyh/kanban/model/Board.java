package com.oognuyh.kanban.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Board {
    
    @Id
    private String id;

    @Size(max = 20, message = "20자 이하로 입력하세요.")
    @NotBlank(message = "제목을 입력하세요.")
    private String title;

    @NotBlank
    private String userId;

    @Valid
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @Valid
    @Builder.Default
    private List<Column> columns = new ArrayList<>();

    public static Board init(String userId) {
        List<Column> columns = Arrays.asList(
            Column.builder().title("Backlog").build(),
            Column.builder().title("To do").build(),
            Column.builder().title("In progress").build(),
            Column.builder().title("Done").build()
        );

        List<Tag> tags = Arrays.asList(
            Tag.builder().name("frontend").color("#4B29DD").textColor("white").build(),
            Tag.builder().name("backend").color("#5F8079").textColor("white").build(),
            Tag.builder().name("devops").color("#DD2982").textColor("white").build(),
            Tag.builder().name("ui/ux").color("#63153C").textColor("white").build()
        );

        return Board.builder()
            .title("New Board")
            .userId(userId)
            .columns(columns)
            .tags(tags)
            .build();
    }
}