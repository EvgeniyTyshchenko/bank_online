package ru.bankonline.project.dto;

import lombok.*;
import org.modelmapper.ModelMapper;
import ru.bankonline.project.entity.Card;
import ru.bankonline.project.constants.Currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/***
 * Класс, представляющий DTO (Data Transfer Object) для карты
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CardDTO implements DTO {

    private String cardNumber;
    private String cvv;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;

    /***
     * Преобразует объект Card в объект CardDTO с помощью ModelMapper
     * @param card объект Card
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return объект CardDTO
     */
    public static CardDTO convertCardToDTO(Card card, ModelMapper modelMapper) {
        return modelMapper.map(card, CardDTO.class);
    }

    /***
     * Преобразует список объектов Card в список объектов CardDTO с помощью ModelMapper
     * @param cards список объектов Card
     * @param modelMapper экземпляр класса ModelMapper для преобразования
     * @return список объектов CardDTO
     */
    public static List<CardDTO> convertListCardsToDTO(List<Card> cards, ModelMapper modelMapper) {
        List<CardDTO> cardDTOs = new ArrayList<>();
        for (Card card : cards) {
            CardDTO cardDTO = new CardDTO();
            modelMapper.map(card, cardDTO);
            cardDTOs.add(cardDTO);
        }
        return cardDTOs;
    }
}