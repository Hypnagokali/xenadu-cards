package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.HelpfulLinkDto;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.*;

import javax.inject.Inject;
import java.util.Optional;

@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "cdi",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = {GenericEntityFactory.class }
)
@Slf4j
public abstract class HelpfulLinkMapper {

    private CardService cardService;


    abstract public HelpfulLinkDto mapToDto(HelpfulLink helpfulLink);
    abstract public HelpfulLink mapToEntity(HelpfulLinkDto helpfulLinkDto);

    @Inject
    public void setCardService(CardService cardService) {
        this.cardService = cardService;
    }

    @AfterMapping
    void mapCardToCardId(@MappingTarget HelpfulLinkDto dto, HelpfulLink helpfulLink) {
        if (helpfulLink.getCard() != null) {
            dto.setCardId(helpfulLink.getCard().getId());
        }
    }

    @AfterMapping
    void mapCardIdBackToCard(@MappingTarget HelpfulLink helpfulLink, HelpfulLinkDto dto) {
        if (dto.getCardId() > 0) {
            final Optional<Card> card = cardService.findById(dto.getCardId());
            if (card.isEmpty()) {
                log.warn("Tried to load card with id = {} but result is empty", dto.getCardId());
            } else {
                helpfulLink.setCard(card.get());
            }
        }
    }
}
