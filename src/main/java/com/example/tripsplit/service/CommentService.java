package com.example.tripsplit.service;

import com.example.tripsplit.dto.CommentRequest;
import com.example.tripsplit.entity.*;
import com.example.tripsplit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public List<Comment> findByTrip(Long tripId) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));
        return commentRepository.findByTripOrderByCreatedAtAsc(trip);
    }

    public void addComment(Long tripId, CommentRequest request, Authentication authentication) {
        Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));
        User author = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        commentRepository.save(Comment.builder()
                .text(request.getText())
                .createdAt(LocalDateTime.now())
                .trip(trip)
                .author(author)
                .build());
    }
}
