package ru.bankonline.project.services.savingsaccountsservice;

import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;

import java.math.BigDecimal;

public interface SavingsAccountsService {
    void openSavingAccountToTheCustomer(Integer passportSeries, Integer passportNumber);

    void closeSavingAccount(Integer passportSeries, Integer passportNumber, String accountNumber);

    String addMoneyToTheAccountThroughTheCashier(Integer passportSeries, Integer passportNumber,
                                                 String accountNumber, BigDecimal amount);

    void transferFromCardToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                          String senderCardNumber, String recipientSavingsAccountNumber, BigDecimal amount);

    String checkBalance(Integer passportSeries, Integer passportNumber, String savingsAccountNumber);

    void checkIfTheSavingAccountIsNotClosedOrBlocked(SavingsAccount savingsAccount);

    SavingsAccount checkSavingAccountExists(Customer customer, String accountNumber);
}