package ru.bankonline.project.services.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DepositScheduler {

    private final SavingsAccountsService savingsAccountsService;
    private final TransactionsService transactionsService;

    @Autowired
    public DepositScheduler(SavingsAccountsService savingsAccountsService, TransactionsService transactionsService) {
        this.savingsAccountsService = savingsAccountsService;
        this.transactionsService = transactionsService;
    }

    @Transactional
    public void deposit() {
        List<SavingsAccount> savingsAccounts = savingsAccountsService.findAllToSavingsAccountsRepository();
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
                savingsAccountsService.saveRepositorySavingsAccounts(account);
                transactionsService.transactionAccrualOfInterestOnTheDeposit(account.getCustomerId(), account, amountAccruedInterest);
                log.info("На номер сберегательного счета {} произведено начисление процентов в размере {} RUB",
                        account.getAccountNumber(), amountAccruedInterest);
            }
        }
    }
}