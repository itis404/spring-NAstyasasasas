package com.example.tripsplit.converter;

import com.example.tripsplit.dto.ExpenseDto;
import com.example.tripsplit.entity.Expense;
import com.example.tripsplit.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpenseToExpenseDtoConverter implements Converter<Expense, ExpenseDto> {

    private final CurrencyService currencyService;

    @Override
    public ExpenseDto convert(Expense expense) {

        String payerName = expense.getPayer() != null ? expense.getPayer().getUsername() : "Неизвестно";

        Double amountInEur = expense.getAmount().doubleValue() * currencyService.getUsdToEurRate();

        return new ExpenseDto(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                payerName,
                expense.getPayer() != null ? expense.getPayer().getEmail() : null,
                amountInEur
        );
    }
}