package com.example.tripsplit.repository;

import com.example.tripsplit.entity.Expense;
import com.example.tripsplit.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTrip(Trip trip);

    @Query("select e from Expense e where e.trip.id = :tripId and e.amount > (select avg(e2.amount) from Expense e2 where e2.trip.id = :tripId)")
    List<Expense> findExpensesGreaterThanAverage(@Param("tripId") Long tripId);
}
