package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

    Optional<Customer> findByPassportSeriesAndPassportNumber(Integer passportSeries, Integer passportNumber);

    @Query("SELECT c FROM Customer c JOIN c.cards card WHERE card.cardNumber = :cardNumber")
    Optional<Customer> findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Customer c JOIN c.savingsAccounts savingAccount WHERE savingAccount.accountNumber = :accountNumber")
    Optional<Customer> findBySavingAccountNumber(String accountNumber);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.passportSeries = :passportSeries AND c.passportNumber = :passportNumber")
    Optional<Integer> findPassportDuplicates(Integer passportSeries, Integer passportNumber);
}