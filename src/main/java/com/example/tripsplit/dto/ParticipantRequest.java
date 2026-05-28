package com.example.tripsplit.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ParticipantRequest {
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;
}
