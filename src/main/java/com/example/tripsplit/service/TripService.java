package com.example.tripsplit.service;

import com.example.tripsplit.dao.TripDao;
import com.example.tripsplit.dto.TripRequest;
import com.example.tripsplit.entity.*;
import com.example.tripsplit.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TripService {
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final TripDao tripDao;
    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final CommentRepository commentRepository;

    public List<Trip> findCurrentUserTrips(Authentication authentication) {
        User user = getCurrentUser(authentication);

        List<Trip> trips = new ArrayList<>();

        trips.addAll(tripRepository.findByOwner(user));

        for (TripParticipant participation : tripParticipantRepository.findByUser(user)) {
            Trip trip = participation.getTrip();

            boolean alreadyAdded = trips.stream()
                    .anyMatch(existingTrip -> existingTrip.getId().equals(trip.getId()));

            if (!alreadyAdded) {
                trips.add(trip);
            }
        }

        return trips;
    }

    public List<Trip> searchTrips(String destination, Authentication authentication) {
        User user = getCurrentUser(authentication);
        return tripDao.searchTrips(destination, user);
    }

    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));
    }

    public Trip getTripForCurrentUser(Long tripId, Authentication authentication) {
        Trip trip = getTripById(tripId);
        User currentUser = getCurrentUser(authentication);

        boolean isOwner = trip.getOwner().getId().equals(currentUser.getId());
        boolean isParticipant = tripParticipantRepository.findByTripAndUser(trip, currentUser).isPresent();

        if (!isOwner && !isParticipant) {
            throw new IllegalArgumentException("У вас нет доступа к этой поездке");
        }

        return trip;
    }

    public boolean isOwner(Long tripId, Authentication authentication) {
        Trip trip = getTripById(tripId);
        User currentUser = getCurrentUser(authentication);
        return trip.getOwner().getId().equals(currentUser.getId());
    }

    public void checkOwner(Long tripId, Authentication authentication) {
        if (!isOwner(tripId, authentication)) {
            throw new IllegalArgumentException("Редактировать или удалять поездку может только создатель");
        }
    }

    public void createTrip(TripRequest request, Authentication authentication) {
        User owner = getCurrentUser(authentication);

        if (request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        Trip savedTrip = tripRepository.save(Trip.builder()
                .title(request.getTitle())
                .destination(request.getDestination())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .owner(owner)
                .build());

        tripParticipantRepository.save(TripParticipant.builder().trip(savedTrip).user(owner).build());
    }

    public void updateTrip(Long id, TripRequest request) {

        if (request.getStartDate() != null && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Дата окончания не может быть раньше даты начала");
        }

        Trip trip = getTripById(id);
        trip.setTitle(request.getTitle());
        trip.setDestination(request.getDestination());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setDescription(request.getDescription());
        trip.setImageUrl(request.getImageUrl());
        tripRepository.save(trip);
    }

    @Transactional
    public void deleteTrip(Long id) {
        Trip trip = getTripById(id);

        List<Expense> expenses = expenseRepository.findByTrip(trip);

        for (Expense expense : expenses) {
            List<ExpenseShare> shares = expenseShareRepository.findByExpense(expense);
            expenseShareRepository.deleteAll(shares);
        }

        expenseRepository.deleteAll(expenses);

        List<TripParticipant> participants = tripParticipantRepository.findByTrip(trip);
        tripParticipantRepository.deleteAll(participants);

        commentRepository.deleteAll(commentRepository.findByTripOrderByCreatedAtAsc(trip));

        tripRepository.delete(trip);
    }

    public void addParticipant(Long tripId, String email) {
        Trip trip = getTripById(tripId);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (tripParticipantRepository.findByTripAndUser(trip, user).isPresent()) {
            throw new IllegalArgumentException("Пользователь уже добавлен в поездку");
        }


        tripParticipantRepository.save(TripParticipant.builder().trip(trip).user(user).build());
    }

    public List<TripParticipant> getParticipants(Long tripId) {
        return tripParticipantRepository.findByTrip(getTripById(tripId));
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Текущий пользователь не найден"));
    }
}
