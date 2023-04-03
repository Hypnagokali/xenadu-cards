package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.dto.HelpfulLinkDto;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.util.XenaduDateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

@Mapper(
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {HelpfulLinkMapper.class, AlternativeAnswerMapper.class, GenericEntityFactory.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        imports = {XenaduDateTimeFormatter.class},
        componentModel = "cdi"
)
@Slf4j
public abstract class CardMapper {

    @Mapping(target = "lastLearned", expression = "java(XenaduDateTimeFormatter.localDateTimeToIsoString(card.getLastLearned()))")
    public abstract CardDto mapToDto(Card card);

    @Mapping(target = "lastLearned", expression = "java(XenaduDateTimeFormatter.isoStringToLocalDateTime(cardDto.getLastLearned()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "helpfulLinks", ignore = true)
    public abstract Card mapToEntity(CardDto cardDto, @Context CardSetService cardSetService, @Context HelpfulLinkMapper helpfulLinkMapper);

    @AfterMapping
    void mapCardToCardId(@MappingTarget CardDto cardDto, Card card) {
        if (card.getCardSet() != null) {
            cardDto.setCardSetId(card.getCardSet().getId());
        }
    }

    @AfterMapping
    void mapCardIdToCard(@MappingTarget Card card,
                         CardDto cardDto,
                         @Context CardSetService cardSetService,
                         @Context HelpfulLinkMapper helpfulLinkMapper
    ) {
        if (cardSetService == null) {
            log.warn("cardSetService is NULL, it must set manually :(");
            return;
        }

        if (cardDto.getCardSetId() > 0) {
            cardSetService.findById(cardDto.getCardSetId())
                    .ifPresent(card::setCardSet);
        }

        card.getHelpfulLinks().clear();
        for (HelpfulLinkDto helpfulLink : cardDto.getHelpfulLinks()) {
            final HelpfulLink hlEntity = helpfulLinkMapper.mapToEntity(helpfulLink);
            card.addLink(hlEntity);
        }
    }

}
