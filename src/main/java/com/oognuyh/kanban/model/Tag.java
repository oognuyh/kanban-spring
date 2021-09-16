package com.oognuyh.kanban.model;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    
    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    private String color;

    private String textColor;
}
