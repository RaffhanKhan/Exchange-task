package com.exchange.currency.service;

import com.exchange.currency.model.ExchangeRate;
import com.exchange.currency.repository.ExchangeRateRepo;
import com.exchange.currency.utils.ExchangeConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ExchangeServiceImpl.class);

    private final ExchangeRateRepo exchangeRateRepo;

    private final Environment environment;
    private final RestTemplate restTemplate;

    @Transactional //used to have DB rollback in case of failure
    @Override
    public Map<String, Object> getExchangeRate(String targetCurrency) {
        Map<String, Object> response = new HashMap<>();

        List<ExchangeRate> rates = exchangeRateRepo.findByDateAndTargetCurrency(LocalDate.now().minusDays(2), targetCurrency);
        if (rates.isEmpty()) {
            try {
                String url = String.format(environment.getProperty("external.api.url")+"/latest?base=%s&symbols=%s", "USD", targetCurrency);

              LOGGER.debug("Fetching exchange rate from external API: {}", url);


                Map<String, Object> restResponse = restTemplate.getForObject(url, Map.class);
                LOGGER.debug(ExchangeConstants.RESPONSE + ":{}", restResponse);

                if (restResponse == null || !restResponse.containsKey("rates")) {
                    response.put(ExchangeConstants.STATUS, "error");
                    response.put("message", "No rate found for target currency.");
                    return response;
                }

                Map<String, Object> ratesMap = (Map<String, Object>) restResponse.get("rates");
                Double rate = (Double) ratesMap.get(targetCurrency);
                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setDate(LocalDate.now());
                exchangeRate.setSourceCurrency("USD");
                exchangeRate.setTargetCurrency(targetCurrency);
                exchangeRate.setRate(rate);
                exchangeRateRepo.save(exchangeRate);
                rates.add(exchangeRate);

                response.put(ExchangeConstants.RESPONSE, rates);
                response.put(ExchangeConstants.STATUS, ExchangeConstants.SUCCESS);

            }
            catch(RuntimeException e){
                response.put("status", "error");
                response.put("message", e.getMessage());
                return response;
            }
        }

        response.put(ExchangeConstants.RESPONSE, rates);
        response.put(ExchangeConstants.STATUS, ExchangeConstants.SUCCESS);
        return response;
    }


    @Override
    public Map<String, Object> getHistoricalRates(String targetCurrency) {
        Map<String, Object> response = new HashMap<>();
        List<ExchangeRate> exchangeRates = exchangeRateRepo.findTop3ByTargetCurrencyOrderByDateDesc(targetCurrency);
        response.put(ExchangeConstants.RESPONSE, exchangeRates);
        response.put(ExchangeConstants.STATUS, ExchangeConstants.SUCCESS);
        return response;
    }

}
