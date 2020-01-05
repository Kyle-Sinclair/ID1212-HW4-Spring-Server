package se.kth.id1212.appserv.bank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import se.kth.id1212.appserv.bank.domain.Currency;


/**
 * Contains all database access concerning different currencies.
 */
@Repository
@Transactional//(propagation = Propagation.MANDATORY)

public interface CurrencyRepository extends CrudRepository<Currency, Long> {
    //TODO change the currency from util to our own one

    Currency findByName(@Param("name") String name);





}
