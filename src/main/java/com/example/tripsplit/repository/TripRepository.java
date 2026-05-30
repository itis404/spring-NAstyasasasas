package com.example.tripsplit.repository;

import com.example.tripsplit.entity.Trip;
import com.example.tripsplit.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findByOwner(User owner);
}
