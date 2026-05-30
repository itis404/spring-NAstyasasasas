package com.example.tripsplit.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_participants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TripParticipant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
