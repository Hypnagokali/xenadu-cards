package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.mapper.CardMapper;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.mapper.HelpfulLinkMapper;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Path("/api/card-sets/{cardSetId}/cards")
@RequiredArgsConstructor
@Authenticated
public class CardController {

    private final CardService cardService;
    private final CardSetService cardSetService;
    private final GetUserInfo getUserInfo;
    private final HelpfulLinkMapper helpfulLinkMapper;

    private final CardMapper cardMapper;

    @PostConstruct
    public void init() {
        helpfulLinkMapper.setCardService(cardService);
    }

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CardDto createCard(@PathParam("cardSetId") long cardSetId, CardDto cardDto) {
        // todo: check userId
        if (cardDto.getCardSetId() > 0 && cardDto.getCardSetId() != cardSetId) {
            throw new RestBadRequestException("This card does not belong to the given cardSet with Id = " + cardSetId);
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
        final Card card = cardService.findById(cardId).orElseThrow(() -> new RestBadRequestException("No Card with this id"));
        if (card.getCardSet().getUserId() == userInfo.getId()) {
            cardService.deleteCardById(cardId);
        } else {
            throw new RestForbiddenException();
        }

    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> allCardsOfCardSet(@PathParam("cardSetId") long cardSetId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        final CardSet cardSet = cardSetService.findById(cardSetId).orElseThrow(RestBadRequestException::new);

        if (cardSet.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }

        return cardService.findAllByCardSetId(cardSetId)
                .stream().map(cardMapper::mapToDto)
                .collect(Collectors.toList());
    }


    @GET
    @Path("/rep-state/{repState}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> showAllByRepetitionState(@PathParam("cardSetId") long cardSetId, @PathParam("repState") int repState) {
        List<Card> cardList = cardService.findAllByRepState(cardSetId, repState);
        cardList.sort(Comparator.comparing(Card::getId));

        return cardList;
    }


}
