package com.example.tripsplit.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "trips")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Trip {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
}
