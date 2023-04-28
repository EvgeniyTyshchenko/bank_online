package ru.bankonline.project.services.scheduler;

import org.springframework.stereotype.Component;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.entity.Transaction;
import ru.bankonline.project.entity.enums.Currency;
import ru.bankonline.project.entity.enums.TransactionType;
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DepositScheduler {

    private final SavingsAccountsRepository savingsAccountsRepository;
    private final TransactionsRepository transactionsRepository;

    public DepositScheduler(SavingsAccountsRepository savingsAccountsRepository, TransactionsRepository transactionsRepository) {
        this.savingsAccountsRepository = savingsAccountsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    public void deposit() {
        List<SavingsAccount> savingsAccounts = savingsAccountsRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        for (SavingsAccount account : savingsAccounts) {
            LocalDate openingDate = account.getOpeningDate().toLocalDate();
            LocalDate nextInterestDate = openingDate.plusMonths(1);

            if (currentDate.equals(nextInterestDate)) {
                BigDecimal balance = account.getBalance();
                BigDecimal interestRate = new BigDecimal("0.05");
                BigDecimal amountAccruedInterest = balance.multiply(interestRate).divide(new BigDecimal("12"), 2, RoundingMode.HALF_UP);
                account.setBalance(balance.add(amountAccruedInterest));
                account.setUpdateDate(LocalDateTime.now());
                savingsAccountsRepository.save(account);
                transactionInterestAccruals(account.getCustomerId(), account, amountAccruedInterest);
            }
        }
    }

    private void transactionInterestAccruals(Integer customerId, SavingsAccount savingsAccount, BigDecimal amountAccruedInterest) {
        Transaction transaction = new Transaction(customerId, "[BANK]", savingsAccount.getAccountNumber(),
                amountAccruedInterest, Currency.RUB, TransactionType.CAPITALIZATION, LocalDateTime.now());
        transactionsRepository.save(transaction);
    }
}