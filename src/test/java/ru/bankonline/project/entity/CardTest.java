package ru.bankonline.project.entity;

import org.junit.jupiter.api.Test;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.constants.Status;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CardTest {

    @Test
    void shouldCreateCard() {
        Card card = new Card(1, "4585002566669548", "662", "45854599999985421548",
                BigDecimal.valueOf(0), Currency.RUB);

        assertEquals(1, card.getCustomerId());
        assertEquals("4585002566669548", card.getCardNumber());
        assertEquals("662", card.getCvv());
        assertEquals("45854599999985421548", card.getAccountNumber());
        assertEquals(BigDecimal.ZERO, card.getBalance());
        assertEquals(Currency.RUB, card.getCurrency());
        assertEquals(Status.ACTIVE, card.getStatus());
        assertNotNull(card.getOpeningDate());
        assertNotNull(card.getUpdateDate());
    }
}