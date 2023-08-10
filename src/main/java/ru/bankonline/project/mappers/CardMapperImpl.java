package ru.bankonline.project.mappers;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.bankonline.project.dto.CardDTO;
import ru.bankonline.project.entity.Card;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CardMapperImpl implements CardMapper {

    private final ModelMapper modelMapper;

    @Override
    public CardDTO convertCardToDto(Card card) {
        return modelMapper.map(card, CardDTO.class);
    }

    @Override
    public List<CardDTO> convertListCardsToDTO(List<Card> cards) {
        if(Objects.isNull(cards) || cards.isEmpty())
            return new ArrayList<>();

        return cards.stream()
                .map(card -> modelMapper.map(card, CardDTO.class))
                .toList();
    }
}
