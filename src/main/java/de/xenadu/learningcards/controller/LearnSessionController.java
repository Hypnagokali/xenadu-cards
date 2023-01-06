package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.LearnSession;
import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.dto.StartLearnSessionRequest;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LearnSessionManager;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/api/learn-session")
@RequiredArgsConstructor
public class LearnSessionController {

    private final LearnSessionManager learnSessionManager;
    private final GetUserInfo getUserInfo;
    private final CardSetService cardSetService;

    @Path("/card-set/{cardSetId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Map<String, Object> letMeStartLearning(@PathParam("cardSetId") long cardSetId, @RequestBody StartLearnSessionRequest request) {
        UserInfo userInfo = getUserInfo.authenticatedUser();

        CardSet cardSet = cardSetService.findById(cardSetId).orElseThrow(() -> new RestBadRequestException("No Card Set found"));

        if (cardSet.getUserId() != userInfo.getId()) {
            throw new RestForbiddenException();
        }

        LearnSessionConfig config = new LearnSessionConfig(cardSetId);
        config.setNumberOfNewCards(request.getNumberOfNewCards());
        config.setNumberOfCardsForRepetition(request.getNumberOfCardsForRepetition());
        config.setSpellChecking(request.isSpellChecking());
        config.setOnlyRepetition(request.isOnlyRepetition());
        config.setUserId(userInfo.getId());

        LearnSession learnSession = learnSessionManager.startNewLearnSession(config);

        return new HashMap<>() {{
            put("sessionId", learnSession.getLearnSessionId().getValue());
            put("spellChecking", config.isSpellChecking());
            put("totalNumberOfCards", learnSession.getTotalNumberOfCards());
            put("numberOfCardsPassed", learnSession.getNumberOfCardsPassed());
        }};
    }

}
