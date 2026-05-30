package com.example.tripsplit.repository;

import com.example.tripsplit.entity.Trip;
import com.example.tripsplit.entity.TripParticipant;
import com.example.tripsplit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TripParticipantRepository extends JpaRepository<TripParticipant, Long> {
    List<TripParticipant> findByTrip(Trip trip);
    List<TripParticipant> findByUser(User user);
    Optional<TripParticipant> findByTripAndUser(Trip trip, User user);
}
