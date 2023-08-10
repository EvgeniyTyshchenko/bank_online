package ru.bankonline.project.mappers;

import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.entity.Card;

import java.util.List;

public interface CardMapper {
    CardDTO convertCardToDto(Card card);
    List<CardDTO> convertListCardsToDTO(List<Card> cards);
}
