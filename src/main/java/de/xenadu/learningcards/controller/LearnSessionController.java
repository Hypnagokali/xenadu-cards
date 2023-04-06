package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.*;
import de.xenadu.learningcards.dto.CardDto;
import de.xenadu.learningcards.dto.LearnSessionDto;
import de.xenadu.learningcards.dto.StartLearnSessionRequest;
import de.xenadu.learningcards.exceptions.RestBadRequestException;
import de.xenadu.learningcards.exceptions.RestForbiddenException;
import de.xenadu.learningcards.exceptions.RestNotFoundException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.mapper.CardMapper;
import de.xenadu.learningcards.service.CardSetService;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LearnSessionManager;
import io.quarkus.logging.Log;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/learn-session")
@RequiredArgsConstructor
public class LearnSessionController {

    private final LearnSessionManager learnSessionManager;
    private final GetUserInfo getUserInfo;
    private final CardSetService cardSetService;

    private final CardMapper cardMapper;



    @Path("/card-set/{cardSetId}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto letMeStartLearning(@PathParam("cardSetId") long cardSetId, @RequestBody StartLearnSessionRequest request) {
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

        Log.infof("%s is starting a new learn session", userInfo.getEmail());
        LearnSession learnSession = learnSessionManager.startNewLearnSession(config);
        Log.infof("There are currently %d learn session(s) open", learnSessionManager.getAllLearnSessions().size());

        return new LearnSessionDto(
                learnSession.getLearnSessionId().getValue(),
                learnSession.getConfig().getCardSetId(),
                null,
                learnSession.getNumberOfCardsPassed(),
                learnSession.getTotalNumberOfCards(),
                learnSession.getConfig().isSpellChecking(),
                null,
                null
        );
    }

    @Path("/{sessionId}/current")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto getCurrentCard(@PathParam("sessionId") String sessionId) {
        LearnSession learnSession = learnSessionManager.getLearnSession(new LearnSessionId(sessionId))
                .orElseThrow(() -> new RestBadRequestException("No such learn session"));

        CardDto card = learnSession.getCurrentCard().map(cardMapper::mapToDto).orElseGet(() ->
                learnSession.getNextCard().getCurrentCard()
                        .map(cardMapper::mapToDto)
                        .orElse(null)
        );

        return new LearnSessionDto(
                learnSession.getLearnSessionId().getValue(),
                learnSession.getConfig().getCardSetId(),
                card,
                learnSession.getNumberOfCardsPassed(),
                learnSession.getTotalNumberOfCards(),
                learnSession.getConfig().isSpellChecking(),
                null,
                null
        );
    }

    @Path("/{sessionId}/add-alternative")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto addAlternative(@PathParam("sessionId") String sessionId,
                                          @RequestBody AnswerResult answerResult) {

        LearnSession learnSession = learnSessionManager.getLearnSession(
                new LearnSessionId(sessionId)
            )
            .orElseThrow(() -> new RestBadRequestException("No such learn session"));

        Card currentCard = learnSession.getCurrentCard()
            .orElseThrow(() -> new RestNotFoundException("No current card found"));


        answerResult = learnSession.addNewAnswer(answerResult, currentCard);

        // is called by front end
        // learnSession.commit(answerResult, currentCard);


        return new LearnSessionDto(
            learnSession.getLearnSessionId().getValue(),
            learnSession.getConfig().getCardSetId(),
            cardMapper.mapToDto(currentCard),
            learnSession.getNumberOfCardsPassed(),
            learnSession.getTotalNumberOfCards(),
            learnSession.getConfig().isSpellChecking(),
            answerResult,
            null
        );


    }

    @Path("/{sessionId}/check")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto checkCard(@PathParam("sessionId") String sessionId,
                                     @RequestBody AnswerRequest answerRequest) {
        LearnSession learnSession = learnSessionManager.getLearnSession(
                new LearnSessionId(sessionId)
            )
            .orElseThrow(() -> new RestBadRequestException("No such learn session"));

        Card card = learnSession.getCurrentCard().orElseThrow(() -> new RestBadRequestException("No current card available"));

        AnswerResult answerResult = learnSession.checkAnswer(answerRequest, card);

        if (answerResult.isCorrect()) {
            learnSession.commit(answerResult, card);
        }

        return new LearnSessionDto(
            learnSession.getLearnSessionId().getValue(),
            learnSession.getConfig().getCardSetId(),
            cardMapper.mapToDto(card),
            learnSession.getNumberOfCardsPassed(),
            learnSession.getTotalNumberOfCards(),
            learnSession.getConfig().isSpellChecking(),
            answerResult,
            null
        );
    }


    @Path("/{sessionId}/commit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto commitAnswer(@PathParam("sessionId") String sessionId,
                                     @RequestBody AnswerResult answerResult) {
        LearnSession learnSession = learnSessionManager.getLearnSession(
            new LearnSessionId(sessionId)
            )
            .orElseThrow(() -> new RestBadRequestException("No such learn session"));

        Card card = learnSession.getCurrentCard().orElseThrow(() -> new RestBadRequestException("No current card available"));

        learnSession.commit(answerResult, card);

        return new LearnSessionDto(
                learnSession.getLearnSessionId().getValue(),
                learnSession.getConfig().getCardSetId(),
                cardMapper.mapToDto(card),
                learnSession.getNumberOfCardsPassed(),
                learnSession.getTotalNumberOfCards(),
                learnSession.getConfig().isSpellChecking(),
                answerResult,
                null
        );
    }


    @Path("/{sessionId}/finish")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public LearnSessionDto finishSession(@PathParam("sessionId") String sessionId,
                                         @DefaultValue("false")
                                         @QueryParam("cancel") boolean cancelSession) {
        Log.infof("finish session: %s", sessionId);

        LearnSession learnSession = learnSessionManager.getLearnSession(new LearnSessionId(sessionId))
                .orElseThrow(() -> new RestBadRequestException("No such learn session"));

        learnSessionManager.finish(learnSession);

        if (cancelSession) {
            // Do not save statistics
            Log.info("Learn session was cancelled, therefore it statistics will not be persisted");
        } else {
            Log.info("ToDo: persist statistics of user");
        }


        return new LearnSessionDto(
                learnSession.getLearnSessionId().getValue(),
                learnSession.getConfig().getCardSetId(),
                null,
                learnSession.getNumberOfCardsPassed(),
                learnSession.getTotalNumberOfCards(),
                learnSession.getConfig().isSpellChecking(),
                null,
                learnSession.getStatistics()
        );
    }

}
