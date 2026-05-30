package com.example.tripsplit.controller;

import com.example.tripsplit.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/currency")
public class CurrencyController {
    private final CurrencyService currencyService;

    @GetMapping("/usd-eur")
    public Double getUsdToEurRate() {
        return currencyService.getUsdToEurRate();
    }
}
