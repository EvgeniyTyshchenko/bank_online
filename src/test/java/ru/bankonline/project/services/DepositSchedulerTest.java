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
import ru.bankonline.project.repositories.SavingsAccountsRepository;
import ru.bankonline.project.repositories.TransactionsRepository;
import ru.bankonline.project.services.scheduler.DepositScheduler;

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
    private SavingsAccountsRepository savingsAccountsRepository;
    @Mock
    private TransactionsRepository transactionsRepository;
    @InjectMocks
    private DepositScheduler depositScheduler;

    @Test
    void shouldReceiveInterestAccrual() {
        LocalDate openingDate = LocalDate.now().minusMonths(1);
        SavingsAccount account = new SavingsAccount(1, 1, "45856595231240006963", BigDecimal.valueOf(1_000),
                Currency.RUB, Status.ACTIVE, openingDate.atStartOfDay(), LocalDateTime.now().minusMonths(1));
        when(savingsAccountsRepository.findAll()).thenReturn(List.of(account));
        when(savingsAccountsRepository.findById(account.getAccountId())).thenReturn(Optional.of(account));
        depositScheduler.deposit();

        BigDecimal expectedBalance = new BigDecimal("1004.17");
        SavingsAccount updatedAccount = savingsAccountsRepository.findById(account.getAccountId()).orElse(null);
        Assertions.assertNotNull(updatedAccount);
        Assertions.assertEquals(expectedBalance, updatedAccount.getBalance());
        log.info("Начисление процентов на счет " + account.getAccountNumber());
    }
}