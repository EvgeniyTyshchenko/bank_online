package ru.bankonline.project.entity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SavingsAccountTest {

    private static SavingsAccount savingsAccounts;

    @BeforeAll
    static void setUp() {
        savingsAccounts = new SavingsAccount(1,"55584200000659844587",
                BigDecimal.valueOf(0), Currency.RUB, Status.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    void shouldCreateSavingsAccount() {
        assertNotNull(savingsAccounts);
        assertEquals(1, savingsAccounts.getCustomerId());
        assertEquals("55584200000659844587", savingsAccounts.getAccountNumber());
        assertEquals(BigDecimal.ZERO, savingsAccounts.getBalance());
        assertEquals(Currency.RUB, savingsAccounts.getCurrency());
        assertEquals(Status.ACTIVE, savingsAccounts.getStatus());
    }
}