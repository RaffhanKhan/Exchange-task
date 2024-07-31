package com.exchange.currency.controller;

import com.exchange.currency.service.ExchangeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor  // in place of @Autowited I have used lambok dependency and this Annotation
public class ExchangeController {

    private final ExchangeService exchangeService;

    @GetMapping("/fx")
    public ResponseEntity<Map<String, Object>> getExchangeRates(@RequestParam String targetCurrency) throws Exception {

        return new ResponseEntity<>(exchangeService.getExchangeRate(targetCurrency), HttpStatus.OK);
    }

    @GetMapping("/fx/{targetCurrency}")
    public ResponseEntity<Map<String, Object>> getTargetExchangeRates(@PathVariable String targetCurrency) {
        return new ResponseEntity<>(exchangeService.getHistoricalRates(targetCurrency), HttpStatus.OK);
    }

}
