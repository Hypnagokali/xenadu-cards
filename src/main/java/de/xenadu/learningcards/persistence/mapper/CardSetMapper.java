package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.CardSetDto;
import de.xenadu.learningcards.persistence.entities.CardSet;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {GenericEntityFactory.class }
)
public interface CardSetMapper {

    CardSetDto mapToDto(CardSet cardSet);

    @Mapping(target = "id", ignore = true)
    CardSet mapToEntity(CardSetDto cardSetDto);

    @AfterMapping
    default void calculateNumberOfCards(CardSet cardSet, @MappingTarget CardSetDto cardSetDto) {
        cardSetDto.setNumberOfCards(cardSet.getCards().size());
    }
}
