package com.example.tripsplit.repository;

import com.example.tripsplit.entity.Comment;
import com.example.tripsplit.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTripOrderByCreatedAtAsc(Trip trip);
}
