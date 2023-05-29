package ru.bankonline.project.dto;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.constants.Currency;
import ru.bankonline.project.entity.Card;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardDTOTest {

    private static ModelMapper modelMapper;
    private static Card cardOne;
    private static Card cardTwo;

    @BeforeAll
    static void setUp() {
        modelMapper = new ModelMapper();
        cardOne = new Card(1, "555001444125336", "458", "44500459963585225841", BigDecimal.valueOf(0), Currency.RUB);
        cardTwo = new Card(1, "4401255698523356", "875", "22502366588887411159", BigDecimal.valueOf(0), Currency.RUB);
    }

    @Test
    void shouldConvertToCardDTO() {
        CardDTO cardDTO = CardDTO.convertCardToDTO(cardOne, modelMapper);

        assertEquals(cardOne.getCardNumber(), cardDTO.getCardNumber());
        assertEquals(cardOne.getCvv(), cardDTO.getCvv());
        assertEquals(cardOne.getAccountNumber(), cardDTO.getAccountNumber());
        assertEquals(cardOne.getBalance(), cardDTO.getBalance());
        assertEquals(cardOne.getCurrency(), cardDTO.getCurrency());
    }

    @Test
    void shouldConvertListCardsToDTO() {
        List<Card> cards = new ArrayList<>(List.of(cardOne, cardTwo));
        List<CardDTO> cardDTOs = CardDTO.convertListCardsToDTO(cards, modelMapper);

        assertEquals(2, cardDTOs.size());

        assertEquals("555001444125336", cardDTOs.get(0).getCardNumber());
        assertEquals("458", cardDTOs.get(0).getCvv());
        assertEquals("44500459963585225841", cardDTOs.get(0).getAccountNumber());
        assertEquals(BigDecimal.ZERO, cardDTOs.get(0).getBalance());
        assertEquals(Currency.RUB, cardDTOs.get(0).getCurrency());

        assertEquals("4401255698523356", cardDTOs.get(1).getCardNumber());
        assertEquals("875", cardDTOs.get(1).getCvv());
        assertEquals("22502366588887411159", cardDTOs.get(1).getAccountNumber());
        assertEquals(BigDecimal.ZERO, cardDTOs.get(1).getBalance());
        assertEquals(Currency.RUB, cardDTOs.get(1).getCurrency());
    }
}