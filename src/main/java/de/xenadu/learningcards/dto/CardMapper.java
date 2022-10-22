package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.util.XenaduDateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { HelpfulLinkMapper.class, CardSetService.class },
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        imports = { XenaduDateTimeFormatter.class },
        componentModel = "cdi"
)
@Slf4j
public abstract class CardMapper {

//    CardSetService cardSetService;


    @Mapping(target = "lastLearned", expression = "java(XenaduDateTimeFormatter.localDateTimeToIsoString(card.getLastLearned()))")
    public abstract CardDto mapToDto(Card card);

    @Mapping(target = "lastLearned", expression = "java(XenaduDateTimeFormatter.isoStringToLocalDateTime(cardDto.getLastLearned()))")
    public abstract Card mapToEntity(CardDto cardDto, @Context CardSetService cardSetService);

    @AfterMapping
    void mapCardToCardId(@MappingTarget CardDto cardDto, Card card) {
        if (card.getCardSet() != null) {
            cardDto.setCardSetId(card.getCardSet().getId());
        }
    }

    @AfterMapping
    void mapCardIdToCard(@MappingTarget Card card, CardDto cardDto, @Context CardSetService cardSetService) {
        if (cardSetService == null) {
            log.warn("cardSetService is NULL, it must set manually :(");
            return;
        }

        if (cardDto.getCardSetId() > 0) {
            cardSetService.findById(cardDto.getCardSetId())
                    .ifPresent(card::setCardSet);
        }

    }

}
