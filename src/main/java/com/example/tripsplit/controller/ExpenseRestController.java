package com.example.tripsplit.controller;

import com.example.tripsplit.converter.ExpenseToExpenseDtoConverter;
import com.example.tripsplit.dto.*;
import com.example.tripsplit.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/trips/{tripId}/expenses")
public class ExpenseRestController {
    private final ExpenseService expenseService;
    private final ExpenseToExpenseDtoConverter expenseConverter;

    @GetMapping
    public List<ExpenseDto> getExpenses(@PathVariable Long tripId) {
        return expenseService.findByTrip(tripId).stream().map(expenseConverter::convert).toList();
    }

    @PostMapping
    public void addExpense(@PathVariable Long tripId, @Valid @RequestBody ExpenseRequest expenseRequest, Authentication authentication) {
        expenseService.addExpense(tripId, expenseRequest, authentication);
    }

    @DeleteMapping("/{expenseId}")
    public void deleteExpense(@PathVariable Long expenseId, Authentication authentication) {
        expenseService.deleteExpense(expenseId, authentication);
    }
}
