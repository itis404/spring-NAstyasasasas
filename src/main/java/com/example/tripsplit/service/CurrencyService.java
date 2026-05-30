package com.example.tripsplit.service;

import com.example.tripsplit.client.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final RestTemplate restTemplate;

    @Cacheable("currencyRates")
    public Double getUsdToEurRate() {
        try {
            String url = "https://open.er-api.com/v6/latest/USD";
            CurrencyResponse response = restTemplate.getForObject(url, CurrencyResponse.class);

            if (response == null || response.getRates() == null || response.getRates().get("EUR") == null) {
                return 0.92;
            }

            return response.getRates().get("EUR");
        } catch (Exception exception) {
            return 0.92;
        }
    }
}
