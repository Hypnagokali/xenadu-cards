package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.AlternativeAnswerDto;
import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.dto.LessonDto;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.mapper.AlternativeAnswerMapper;
import de.xenadu.learningcards.persistence.mapper.CardMapper;
import de.xenadu.learningcards.persistence.mapper.HelpfulLinkMapper;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LessonService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/api/card-sets/{cardSetId}/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;
    private final CardSetService cardSetService;
    private final GetUserInfo getUserInfo;
    private final HelpfulLinkMapper helpfulLinkMapper;

    private final AlternativeAnswerMapper alternativeAnswerMapper;
    private final LessonService lessonService;

    private final CardMapper cardMapper;

    @PostConstruct
    public void init() {
        helpfulLinkMapper.setCardService(cardService);
    }


    /**
     * Get all lessons that a card is assigned to.
     *
     * @param cardId ID of the given card.
     * @return List of lessons.
     */
    @GET
    @Path("/{cardId}/lessons")
    public List<LessonDto> lessonsOfCard(@PathParam("cardId") long cardId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();
        final Card card = cardService.findById(cardId)
            .orElseThrow(() -> new RestBadRequestException("No Card with this id"));
        if (card.getCardSet().getUserId() == userInfo.getId()) {
            List<Lesson> lessons = lessonService.findAllByCardId(card.getId());

            return lessons.stream()
                .map(l ->
                    new LessonDto(l, card.getCardSet().getId())
                ).toList();
        } else {
            throw new RestForbiddenException();
        }
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CardDto createCard(@PathParam("cardSetId") long cardSetId, CardDto cardDto) {
        // todo: check userId
        if (cardDto.getCardSetId() > 0 && cardDto.getCardSetId() != cardSetId) {
            throw new RestBadRequestException(
                "This card does not belong to the given cardSet with Id = " + cardSetId);
        }

        if (cardDto.getCardSetId() == 0) {
            cardDto.setCardSetId(cardSetId);
        }

        final Card card = cardMapper.mapToEntity(cardDto, cardSetService, helpfulLinkMapper);

        if (card.getCardSet() == null) {
            throw new RestBadRequestException("CardSet not found. Id = " + cardSetId);
        }

        cardService.saveCard(card);

        return cardMapper.mapToDto(card);
    }

    @PUT
    @Path("/{cardId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CardDto updateCard(CardDto cardDto, @PathParam("cardId") long cardId) {
        final Card card = cardMapper.mapToEntity(cardDto, cardSetService, helpfulLinkMapper);

        return cardMapper.mapToDto(cardService.saveCard(card));
    }

    @DELETE
    @Path("/{cardId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ResponseStatus(204)
    public void deleteCard(@PathParam("cardId") long cardId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();
        final Card card = cardService.findById(cardId)
            .orElseThrow(() -> new RestBadRequestException("No Card with this id"));
        if (card.getCardSet().getUserId() == userInfo.getId()) {
            cardService.deleteCardById(cardId);
        } else {
            throw new RestForbiddenException();
        }

    }

    @GET
    @Path("/{cardId}")
    @Produces(MediaType.APPLICATION_JSON)
    public CardDto fetchCard(@PathParam("cardId") long cardId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();
        final Card card = cardService.findById(cardId)
            .orElseThrow(() -> new RestBadRequestException("No Card with this id"));
        if (card.getCardSet().getUserId() == userInfo.getId()) {
            return cardMapper.mapToDto(card);
        } else {
            throw new RestForbiddenException();
        }
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> allCardsOfCardSet(@PathParam("cardSetId") long cardSetId) {
        assertCardSetIsOwnedByUser(cardSetId);

        return cardService.findAllByCardSetId(cardSetId)
            .stream().map(cardMapper::mapToDto)
            .collect(Collectors.toList());
    }

    private void assertCardSetIsOwnedByUser(long cardSetId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        final CardSet cardSet =
            cardSetService.findById(cardSetId).orElseThrow(RestBadRequestException::new);

        if (cardSet.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }
    }


    @GET
    @Path("/rep-state/{repState}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> showAllByRepetitionState(@PathParam("cardSetId") long cardSetId,
                                               @PathParam("repState") int repState) {
        List<Card> cardList = cardService.findAllByRepState(cardSetId, repState);
        cardList.sort(Comparator.comparing(Card::getId));

        return cardList;
    }

    /**
     * GET Request: retrieving {@link AlternativeAnswer} to a specific card as DTO.
     *
     * @return List of AlternativeAnswer
     */
    @GET
    @Path("/{cardId}/{cardSide}")
    public List<AlternativeAnswerDto> alternativeAnswersOfCard(
        @PathParam("cardSetId") long cardSetId,
        @PathParam("cardId") long cardId,
        @PathParam("cardSide") String cardSide
    ) {
        assertCardSetIsOwnedByUser(cardSetId);


        Card cardWithAlternatives = cardService.findByIdAndFetchAlternatives(cardId);

        List<AlternativeAnswer> alternatives;
        if ("back".equalsIgnoreCase(cardSide)) {
            alternatives = cardWithAlternatives.getAlternativeAnswersForBackSide();
        } else {
            alternatives = cardWithAlternatives.getAlternativeAnswersForFrontSide();
        }

        return alternatives.stream()
            .map(alternativeAnswerMapper::mapToDto)
            .toList();
    }


}
