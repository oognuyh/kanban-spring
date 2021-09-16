package com.oognuyh.kanban.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Column {

    @Size(max = 20, message = "20자 이하로 입력하세요.")
    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    
    @Valid
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}