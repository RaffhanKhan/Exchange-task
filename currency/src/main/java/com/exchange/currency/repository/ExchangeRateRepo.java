package com.exchange.currency.repository;

import com.exchange.currency.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExchangeRateRepo extends JpaRepository<ExchangeRate, Long> {

    List<ExchangeRate> findByDate(LocalDate now);

    @Query(value = "SELECT * FROM exchange_rate WHERE target_currency = :targetCurrency ORDER BY date DESC LIMIT 3", nativeQuery = true)
    List<ExchangeRate> findTop3ByTargetCurrencyOrderByDateDesc(@Param("targetCurrency") String targetCurrency);

    List<ExchangeRate> findByDateAndTargetCurrency(LocalDate now, String targetCurrency);
}
