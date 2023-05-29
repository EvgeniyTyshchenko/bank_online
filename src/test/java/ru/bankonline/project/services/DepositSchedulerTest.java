package ru.bankonline.project.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;
import ru.bankonline.project.entity.SavingsAccount;
import ru.bankonline.project.services.savingsaccountsservice.SavingsAccountsService;
import ru.bankonline.project.services.scheduler.DepositScheduler;
import ru.bankonline.project.services.transactionsservice.TransactionsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DepositSchedulerTest {

    @Mock
    private SavingsAccountsService savingsAccountsService;
    @Mock
    private TransactionsService transactionsService;
    @InjectMocks
    private DepositScheduler depositScheduler;

    @Test
    void shouldPerformInterestAccrualOnSavingsAccounts() {
        LocalDate openingDate = LocalDate.now().minusMonths(1);
        SavingsAccount account = new SavingsAccount(1, 1, "45856595231240006963", BigDecimal.valueOf(1_000),
                Currency.RUB, Status.ACTIVE, openingDate.atStartOfDay(), LocalDateTime.now().minusMonths(1));

        when(savingsAccountsService.findAllToSavingsAccountsRepository()).thenReturn(List.of(account));
        when(savingsAccountsService.findByIdToSavingsAccountsRepository(account.getAccountId())).thenReturn(Optional.of(account));
        depositScheduler.performInterestAccrualOnSavingsAccounts();

        BigDecimal expectedBalance = new BigDecimal("1004.17");
        SavingsAccount updatedAccount = savingsAccountsService.findByIdToSavingsAccountsRepository(account.getAccountId()).orElse(null);
        Assertions.assertNotNull(updatedAccount);
        Assertions.assertEquals(expectedBalance, updatedAccount.getBalance());
        log.info("Начисление процентов на счет " + account.getAccountNumber());
    }
}