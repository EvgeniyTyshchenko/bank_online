package ru.bankonline.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bankonline.project.entity.Customer;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, Integer> {

    Customer findByPassportSeriesAndPassportNumber(Integer passportSeries, Integer passportNumber);

    @Query("SELECT c FROM Customer c JOIN c.cards card WHERE card.cardNumber = :cardNumber")
    Customer findByCardNumber(String cardNumber);

    @Query("SELECT c FROM Customer c JOIN c.savingsAccounts savingAccount WHERE savingAccount.accountNumber = :accountNumber")
    Customer findBySavingAccountNumber(String accountNumber);
}