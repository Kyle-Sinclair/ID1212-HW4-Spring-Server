package se.kth.id1212.appserv.bank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import se.kth.id1212.appserv.bank.domain.Currency;
import se.kth.id1212.appserv.bank.domain.ExchangeRate;


/**
 * Contains all database access concerning different currencies.
 */

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate, Long> {


    ExchangeRate findByFromCurrencyAndToCurrency(@Param("fromCurrency") Currency fromCurrency, @Param("toCurrency") Currency toCurrency);

}
