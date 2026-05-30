package com.example.tripsplit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class TripRequest {
    @NotBlank(message = "Название поездки обязательно")
    private String title;

    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String imageUrl;
}
