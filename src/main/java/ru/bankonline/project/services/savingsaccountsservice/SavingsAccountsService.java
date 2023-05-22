package ru.bankonline.project.services.savingsaccountsservice;

import ru.bankonline.project.entity.Customer;
import ru.bankonline.project.entity.SavingsAccount;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsAccountsService {
    void openSavingAccountToTheCustomer(Integer passportSeries, Integer passportNumber);

    String closeAccountAndWithdrawMoneyThroughCashier(Integer passportSeries, Integer passportNumber, String accountNumber);

    String addMoneyToTheAccountThroughTheCashier(Integer passportSeries, Integer passportNumber,
                                                 String accountNumber, BigDecimal amount);

    String checkBalance(Integer passportSeries, Integer passportNumber, String savingsAccountNumber);

    void transferFromSavingsAccountToSavingsAccount(Integer passportSeries, Integer passportNumber,
                                                    String senderSavingsAccountNumber, String recipientSavingsAccountNumber, BigDecimal amount);

    void checkIfTheSavingAccountIsNotClosedOrBlocked(SavingsAccount savingsAccount);

    SavingsAccount checkSavingAccountExists(Customer customer, String accountNumber);

    void checkIfThereIsMoneyOnTheSavingAccount(SavingsAccount savingsAccount);

    void saveRepositorySavingsAccounts(SavingsAccount savingsAccount);

    List<SavingsAccount> findAllToSavingsAccountsRepository();

    List<SavingsAccount> findByCustomerIdToSavingsAccountsRepository(Integer customerId);
}