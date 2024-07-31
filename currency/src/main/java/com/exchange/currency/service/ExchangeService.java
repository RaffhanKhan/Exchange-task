package com.exchange.currency.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface ExchangeService {
    public Map<String, Object> getExchangeRate(String targetCurrency) throws Exception;

    public Map<String, Object> getHistoricalRates(String targetCurrency);
}
