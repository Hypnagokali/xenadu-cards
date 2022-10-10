package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.persistence.entities.CardSet;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardSetMapper {

    CardSetDto mapToDto(CardSet cardSet);
    CardSet mapToEntity(CardSetDto cardSetDto);

    @AfterMapping
    default void calculateNumberOfCards(CardSet cardSet, @MappingTarget CardSetDto cardSetDto) {
        cardSetDto.setNumberOfCards(cardSet.getCards().size());
    }
}
