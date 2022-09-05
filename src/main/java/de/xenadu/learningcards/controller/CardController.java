package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.CardService;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;

@Path("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GET
    @Path("/rep-state/{repState}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> showAllByRepetitionState(@PathParam("repState") int repState) {
        List<Card> cardList = cardService.findAllByRepState(repState);
        cardList.sort(Comparator.comparing(Card::getId));

        return cardList;
    }


}
