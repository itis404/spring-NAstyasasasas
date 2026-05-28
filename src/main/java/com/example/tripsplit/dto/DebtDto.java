package com.example.tripsplit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter @AllArgsConstructor
public class DebtDto {
    private String fromUser;
    private String toUser;
    private BigDecimal amount;
}
