package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.*;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("ALL")
@QuarkusTest
public class LearnSessionAndManagementIT {

    @Inject
    CardSetService cardSetService;

    @Inject
    CardService cardService;

    @Inject
    CardDistributionStrategy cardDistributionStrategy;


    LearnSessionManager learnSessionManager;


    long cardSetId;

    @BeforeEach
    void setUp() {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime rep1DateTime = LocalDateTime.now().minusHours(2);
        LocalDateTime rep3DateTime = LocalDateTime.now().minusDays(2);
        LocalDateTime notInRep3DateTime = LocalDateTime.now().minusDays(2).plusHours(2);
        LocalDateTime rep5DateTime = LocalDateTime.now().minusMonths(1);


        cardSet.addCard(new Card("new 1", "neu 1"));
        cardSet.addCard(new Card("new 2", "neu 2"));

        cardSet.addCard(new Card("new 3", "neu 3"));

        cardSet.addCard(new Card("wrong answer", "falsche antwort", 1, LocalDateTime.now(), false));

        cardSet.addCard(new Card("rep 1/1", "rep 1", 1, rep1DateTime, true));
        cardSet.addCard(new Card("rep 3/1", "rep 3", 3, rep3DateTime, true));
        cardSet.addCard(new Card("rep 3/2 (not in)", "not in rep 3", 3, notInRep3DateTime, true));
        cardSet.addCard(new Card("rep 3/3", "rep 3", 3, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/1", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/2", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/3", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/4", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/5", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/6", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/7", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/8", "rep 5", 5, rep5DateTime, true));
        cardSet.addCard(new Card("rep 5/9", "rep 5", 5, rep5DateTime, true));

        cardSetService.save(cardSet, userInfo);
        cardSetId = cardSet.getId();

        learnSessionManager = new LearnSessionManager(cardService, cardDistributionStrategy, new WordByWordAnswerAuditor());
    }

    @Test
    void whenFinishingALearnSession_ExpectSessionWillRemovedFromSessionManager() {
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionWithAllNewCards(cardSetId));
        learnSession.finish();

        assertThat(learnSessionManager.getLearnSession(learnSession.getLearnSessionId())).isEmpty();
    }

    @Test
    void whenCallCheckAnswer_ExpectCardIsSavedImediately() {
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionWithAllNewCards(cardSetId));
        Optional<Card> new1Card = learnSession.getLearningCards().stream().filter(c -> c.getFront().equals("new 1")).findAny();

        AnswerResult r = learnSession.checkAnswer(new AnswerRequest("neu 1", true), new1Card.get());

        assertThat(r.isCorrect()).isTrue();
        Optional<Card> loadedCard = cardService.findById(new1Card.get().getId());

        assertThat(loadedCard).isNotEmpty();
        assertThat(loadedCard.get().getRepetitionState()).isEqualTo(1);
        assertThat(loadedCard.get().isLastResultWasCorrect()).isTrue();

    }

    private LearnSessionConfig learnSessionWithAllNewCards(long cardSetId) {
        LearnSessionConfig learnSessionConfig = new LearnSessionConfig(cardSetId);
        learnSessionConfig.setNumberOfNewCards(3);

        return learnSessionConfig;
    }

    @Test
    void whenACorrectConfigIsGiven_ExpectNewCardsAsGivenAndCardsForRepetitionWhichAreReadyForRepetition() {
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionWith2NewCardsAnd5Reps(cardSetId));

        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        // wrong answers are always repeated
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("wrong");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 1/");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 3/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 3/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 5/");
    }

    @Test
    void whenTheBoundariesForNewCardsAreGreaterThanNewCards_ExpectTheDifferenceInSetOfCardsForRepetition() {
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionWith4NewCardsAnd3Reps(cardSetId));

        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        // wrong answers will always be repeated:
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("wrong");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 1/");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 3/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 3/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 5/");
    }

    private LearnSessionConfig learnSessionWith4NewCardsAnd3Reps(long cardSetId) {
        LearnSessionConfig learnSessionConfig = new LearnSessionConfig(cardSetId);
        learnSessionConfig.setNumberOfNewCards(4);
        learnSessionConfig.setNumberOfCardsForRepetition(3);

        return learnSessionConfig;
    }

    private LearnSessionConfig learnSessionWith2NewCardsAnd5Reps(long cardSetId) {
        LearnSessionConfig learnSessionConfig = new LearnSessionConfig(cardSetId);
        learnSessionConfig.setNumberOfNewCards(2);
        learnSessionConfig.setNumberOfCardsForRepetition(5);

        return learnSessionConfig;
    }
}
