package ru.bankonline.project.entity;

import org.junit.jupiter.api.Test;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionTest {

    @Test
    void shouldCreateTransactionWithValidParams() {
        Transaction transaction = new Transaction(1, "44452000899954111123", "45888777620125488889",
                BigDecimal.valueOf(1_000), Currency.RUB, TransactionType.INTRANSFER, LocalDateTime.now());
        assertNotNull(transaction);
        assertEquals(1, transaction.getCustomerId());
        assertEquals("44452000899954111123", transaction.getSendersAccountNumber());
        assertEquals("45888777620125488889", transaction.getRecipientAccountNumber());
        assertEquals(BigDecimal.valueOf(1_000), transaction.getAmount());
        assertEquals(Currency.RUB, transaction.getCurrency());
        assertEquals(TransactionType.INTRANSFER, transaction.getTransactionType());
        assertNotNull(transaction.getDateTimeTransaction());
    }
}