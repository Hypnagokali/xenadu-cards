package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.dto.CardMapper;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.extern.api.UserService;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;

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

    private final CardMapper cardMapper;

    @POST
    @Path("")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CardDto createCard(@PathParam("cardSetId") long cardSetId, CardDto cardDto) {
        // todo: check userId
        if (cardDto.getCardSetId() != cardSetId) {
            throw new RestBadRequestException("This card does not belong to the given cardSet with Id = " + cardSetId);
        }
        final Card card = cardMapper.mapToEntity(cardDto, cardSetService);

        if (card.getCardSet() == null) {
            throw new RestBadRequestException("CardSet not found. Id = " + cardSetId);
        }

        cardService.saveCard(card);

        return cardMapper.mapToDto(card);
    }

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CardDto> allCardsOfCardSet(@PathParam("cardSetId") long cardSetId) {
        // check userId
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
