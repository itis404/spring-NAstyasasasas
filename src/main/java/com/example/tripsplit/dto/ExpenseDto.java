package com.example.tripsplit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ExpenseDto {
    private Long id;
    private String title;
    private BigDecimal amount;
    private String payerName;
    private String payerEmail;
    private Double amountInEur;
}