package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import lombok.RequiredArgsConstructor;

@Path("/api/card-sets/{cardSetId}/cards/{cardId}/alternative")
@RequiredArgsConstructor
public class AlternativeAnswerController {

    private final GetUserInfo getUserInfo;
    private final CardSetService cardSetService;
    private final CardService cardService;


    @Path("{alternativeId}")
    @DELETE
    public void delete(@PathParam("cardSetId") long cardSetId,
                       @PathParam("cardId") long cardId,
                       @PathParam("alternativeId") long alternativeId) {

        assertCardSetIsOwnedByUser(cardSetId);

        cardService.removeAlternativeAnswerById(cardId, alternativeId);

    }

    private void assertCardSetIsOwnedByUser(long cardSetId) {
        final UserInfo userInfo = getUserInfo.authenticatedUser();

        final CardSet
            cardSet = cardSetService.findById(cardSetId).orElseThrow(RestBadRequestException::new);

        if (cardSet.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }
    }
}
