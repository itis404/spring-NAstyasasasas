package com.example.tripsplit.repository;

import com.example.tripsplit.entity.Expense;
import com.example.tripsplit.entity.ExpenseShare;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseShareRepository extends JpaRepository<ExpenseShare, Long> {
    List<ExpenseShare> findByExpense(Expense expense);
}
