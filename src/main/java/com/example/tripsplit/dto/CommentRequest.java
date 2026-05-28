package com.example.tripsplit.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommentRequest {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(max = 1000, message = "Комментарий не должен быть длиннее 1000 символов")
    private String text;
}
