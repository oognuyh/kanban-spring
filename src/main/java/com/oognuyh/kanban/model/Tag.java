package com.oognuyh.kanban.model;

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
public class Tag {
    
    @Size(max = 10, message = "10자 이하로 입력하세요.")
    @NotBlank(message = "이름을 입력하세요.")
    private String name;

    private String color;

    private String textColor;
}
