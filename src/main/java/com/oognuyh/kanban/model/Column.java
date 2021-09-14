package com.oognuyh.kanban.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Column {

    private String title;
    
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
}