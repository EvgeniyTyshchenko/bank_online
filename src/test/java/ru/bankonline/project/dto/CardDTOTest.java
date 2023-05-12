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

        assertEquals(cardDTOs.size(), 2);

        assertEquals(cardDTOs.get(0).getCardNumber(), "555001444125336");
        assertEquals(cardDTOs.get(0).getCvv(), "458");
        assertEquals(cardDTOs.get(0).getAccountNumber(), "44500459963585225841");
        assertEquals(cardDTOs.get(0).getBalance(), BigDecimal.ZERO);
        assertEquals(cardDTOs.get(0).getCurrency(), Currency.RUB);

        assertEquals(cardDTOs.get(1).getCardNumber(), "4401255698523356");
        assertEquals(cardDTOs.get(1).getCvv(), "875");
        assertEquals(cardDTOs.get(1).getAccountNumber(), "22502366588887411159");
        assertEquals(cardDTOs.get(1).getBalance(), BigDecimal.ZERO);
        assertEquals(cardDTOs.get(1).getCurrency(), Currency.RUB);
    }
}