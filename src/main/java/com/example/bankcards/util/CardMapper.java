package com.example.bankcards.util;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * Mapper for the entity {@link Card} and its DTO {@link CardDto}
 */
@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(source = "user.id", target = "userId")
    CardDto toDto(Card card);

    @Mapping(source = "userId", target = "user.id")
    Card toEntity(CardDto dto);

    List<CardDto> toDtoList(List<Card> cards);

    @Named("toMaskedDto")
    default CardDto toMaskedDto(Card card) {
        CardDto dto = toDto(card);
        dto.maskNumber();
        return dto;
    }
}
