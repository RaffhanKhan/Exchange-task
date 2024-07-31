package com.exchange.currency.service;
import com.exchange.currency.model.ExchangeRate;
import com.exchange.currency.repository.ExchangeRateRepo;
import com.exchange.currency.utils.ExchangeConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

public class ExchangeServiceImplTests {

    @InjectMocks
    private ExchangeServiceImpl exchangeService;

    @Mock
    private ExchangeRateRepo exchangeRateRepo;

    @Mock
    private Environment environment;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRateDataAvailable() {
        String targetCurrency = "EUR";
        LocalDate date = LocalDate.now().minusDays(2);
        ExchangeRate existingRate = new ExchangeRate();
        existingRate.setDate(date);
        existingRate.setSourceCurrency("USD");
        existingRate.setTargetCurrency(targetCurrency);
        existingRate.setRate(0.85);

        when(exchangeRateRepo.findByDateAndTargetCurrency(date, targetCurrency))
                .thenReturn(List.of(existingRate));

        Map<String, Object> response = exchangeService.getExchangeRate(targetCurrency);

        assertNotNull(response);
        assertEquals(ExchangeConstants.SUCCESS, response.get(ExchangeConstants.STATUS));
        assertEquals(1, ((List<?>) response.get(ExchangeConstants.RESPONSE)).size());
    }

    @Test
    void testGetExchangeRate2() {
        String targetCurrency = "EUR";
        LocalDate date = LocalDate.now().minusDays(2);

        when(exchangeRateRepo.findByDateAndTargetCurrency(date, targetCurrency))
                .thenReturn(List.of());

        String url = "https://api.example.com/latest?base=USD&symbols=" + targetCurrency;
        Map<String, Object> apiResponse = new HashMap<>();
        Map<String, Object> rates = new HashMap<>();
        rates.put(targetCurrency, 0.85);
        apiResponse.put("rates", rates);

        when(environment.getProperty("external.api.url")).thenReturn("https://api.example.com");
        when(restTemplate.getForObject(eq(url), eq(Map.class))).thenReturn(apiResponse);

        Map<String, Object> response = exchangeService.getExchangeRate(targetCurrency);

        assertNotNull(response);
        assertEquals("error", response.get(ExchangeConstants.STATUS));
    }

    @Test
    void testGetExchangeRateApiError() {
        String targetCurrency = "EUR";
        LocalDate date = LocalDate.now().minusDays(2);

        when(exchangeRateRepo.findByDateAndTargetCurrency(date, targetCurrency))
                .thenReturn(List.of());

        String url = "https://api.example.com/latest?base=USD&symbols=" + targetCurrency;

        when(environment.getProperty("external.api.url")).thenReturn("https://api.example.com");
        when(restTemplate.getForObject(eq(url), eq(Map.class))).thenThrow(new RuntimeException("API error"));

        Map<String, Object> response = exchangeService.getExchangeRate(targetCurrency);

        assertNotNull(response);
        assertEquals("error", response.get("status"));
        assertEquals("API error", response.get("message"));
    }

    @Test
    void testGetHistoricalRates() {
        String targetCurrency = "EUR";
        ExchangeRate rate1 = new ExchangeRate();
        rate1.setDate(LocalDate.now().minusDays(1));
        rate1.setSourceCurrency("USD");
        rate1.setTargetCurrency(targetCurrency);
        rate1.setRate(0.85);

        ExchangeRate rate2 = new ExchangeRate();
        rate2.setDate(LocalDate.now().minusDays(2));
        rate2.setSourceCurrency("USD");
        rate2.setTargetCurrency(targetCurrency);
        rate2.setRate(0.84);

        when(exchangeRateRepo.findTop3ByTargetCurrencyOrderByDateDesc(targetCurrency))
                .thenReturn(List.of(rate1, rate2));

        Map<String, Object> response = exchangeService.getHistoricalRates(targetCurrency);

        assertNotNull(response);
        assertEquals(ExchangeConstants.SUCCESS, response.get(ExchangeConstants.STATUS));
        assertEquals(2, ((List<?>) response.get(ExchangeConstants.RESPONSE)).size());
    }
}
