package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.extern.api.UserService;
import io.quarkus.security.Authenticated;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Comparator;
import java.util.List;

@Path("/api/cards")
@RequiredArgsConstructor
@Authenticated
public class CardController {

    private final CardService cardService;
    private final GetUserInfo getUserInfo;

    @GET
    @Path("user")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo testUserInfo() {
        return getUserInfo.authenticatedUser();
    }

    @GET
    @Path("/rep-state/{repState}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Card> showAllByRepetitionState(@PathParam("repState") int repState) {
        List<Card> cardList = cardService.findAllByRepState(repState);
        cardList.sort(Comparator.comparing(Card::getId));

        return cardList;
    }


}
