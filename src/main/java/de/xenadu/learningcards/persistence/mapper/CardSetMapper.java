package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.domain.CardSetInfos;
import de.xenadu.learningcards.dto.CardSetDto;
import de.xenadu.learningcards.exceptions.MissingMappingConfigurationException;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.service.CardSetService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {GenericEntityFactory.class }
)
@Slf4j
public abstract class CardSetMapper {

    private CardSetService cardSetService;

    public void setCardSetService(CardSetService cardSetService) {
        this.cardSetService = cardSetService;
    }

    public abstract CardSetDto mapToDto(CardSet cardSet);

    @Mapping(target = "id", ignore = true)
    public abstract CardSet mapToEntity(CardSetDto cardSetDto);

    @AfterMapping
    void calculateNumberOfCards(CardSet cardSet, @MappingTarget CardSetDto cardSetDto) {
        if (cardSetService == null) {
            log.warn("cardSetService is not set. Some values cannot be mapped");
            cardSetDto.setCardSetInfos(new CardSetInfos(0, 0, 0, 0));
        } else {
            cardSetDto.setCardSetInfos(cardSetService.getCardSetInfos(cardSetDto.getId()));
        }
        cardSetDto.setNumberOfCards(cardSet.getCards().size());
    }
}
