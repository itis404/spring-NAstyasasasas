package com.example.tripsplit.service;

import com.example.tripsplit.dto.*;
import com.example.tripsplit.entity.*;
import com.example.tripsplit.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.math.*;
import java.time.LocalDate;
import java.util.*;

@RequiredArgsConstructor
@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;
    private final TripParticipantRepository tripParticipantRepository;
    private final ExpenseShareRepository expenseShareRepository;

    public List<Expense> findByTrip(Long tripId) {
        return expenseRepository.findByTrip(getTrip(tripId));
    }

    public BigDecimal calculateTotal(Long tripId) {
        BigDecimal total = BigDecimal.ZERO;
        for (Expense expense : findByTrip(tripId)) {
            total = total.add(expense.getAmount());
        }
        return total;
    }

    public void addExpense(Long tripId, ExpenseRequest request, Authentication authentication) {
        Trip trip = getTrip(tripId);
        User payer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        Expense savedExpense = expenseRepository.save(Expense.builder()
                .title(request.getTitle())
                .amount(request.getAmount())
                .expenseDate(LocalDate.now())
                .payer(payer)
                .trip(trip)
                .build());

        List<TripParticipant> participants = tripParticipantRepository.findByTrip(trip);
        if (participants.isEmpty()) return;

        BigDecimal shareAmount = request.getAmount().divide(BigDecimal.valueOf(participants.size()), 2, RoundingMode.HALF_UP);

        for (TripParticipant participant : participants) {
            expenseShareRepository.save(ExpenseShare.builder()
                    .expense(savedExpense)
                    .user(participant.getUser())
                    .amount(shareAmount)
                    .build());
        }
    }

    @Transactional
    public void deleteExpense(Long expenseId, Authentication authentication) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Расход не найден"));

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        if (!expense.getPayer().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Удалять расход может только тот, кто его добавил");
        }

        List<ExpenseShare> shares = expenseShareRepository.findByExpense(expense);
        expenseShareRepository.deleteAll(shares);

        expenseRepository.delete(expense);
    }

    public List<DebtDto> calculateDebts(Long tripId) {
        List<DebtDto> debts = new ArrayList<>();

        for (Expense expense : findByTrip(tripId)) {
            for (ExpenseShare share : expenseShareRepository.findByExpense(expense)) {
                if (!share.getUser().getId().equals(expense.getPayer().getId())) {
                    debts.add(new DebtDto(share.getUser().getUsername(), expense.getPayer().getUsername(), share.getAmount()));
                }
            }
        }

        return debts;
    }

    private Trip getTrip(Long tripId) {
        return tripRepository.findById(tripId).orElseThrow(() -> new IllegalArgumentException("Поездка не найдена"));
    }
}
