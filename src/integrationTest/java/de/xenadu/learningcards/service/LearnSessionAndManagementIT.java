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
        LocalDateTime rep4DateTime = LocalDateTime.now().minusDays(2);
        LocalDateTime notInRep4DateTime = LocalDateTime.now().minusDays(2).plusHours(2);
        LocalDateTime rep6DateTime = LocalDateTime.now().minusMonths(1);


        cardSet.addCard(new Card("new 1", "neu 1"));
        cardSet.addCard(new Card("new 2", "neu 2"));

        cardSet.addCard(new Card("new 3", "neu 3"));

        // Attention: lastAnswerWasCorrect will be ignored in this tests
        cardSet.addCard(new Card("rep 1/1", "rep 1", 1, rep1DateTime, true));
        cardSet.addCard(new Card("rep 4/1", "rep 4", 4, rep4DateTime, true));
        cardSet.addCard(new Card("rep 4/2 (not in)", "not in rep 4", 4, notInRep4DateTime, true));
        cardSet.addCard(new Card("rep 4/3", "rep 4", 4, rep4DateTime, true));

        cardSet.addCard(new Card("rep 6/1", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/2", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/3", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/4", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/5", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/6", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/7", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/8", "rep 6", 6, rep6DateTime, true));
        cardSet.addCard(new Card("rep 6/9", "rep 6", 6, rep6DateTime, true));

        cardSetService.save(cardSet, userInfo);
        cardSetId = cardSet.getId();

        learnSessionManager = new LearnSessionManager(cardService, cardDistributionStrategy, new WordByWordAnswerAuditor(cardService));
    }


    @Test
    void whenCardWasIncorrectlyAnswered_ExpectRepStateIs1() {
        LearnSession learnSession =
            learnSessionManager.startNewLearnSession(learnSessionWithAllNewCards(cardSetId));

        Card cardWithRepState6 = learnSession.getLearningCards().stream()
            .filter(c -> c.getRepetitionState() == 6)
            .findAny()
            .orElseThrow();

        AnswerResult answerResult = learnSession.checkAnswer(
            new AnswerRequest("boo! this should be wrong.", true),
            cardWithRepState6
        );
        learnSession.commit(answerResult, cardWithRepState6);

        assertThat(cardWithRepState6.getRepetitionState()).isEqualTo(1);
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
        learnSession.commit(r, new1Card.get());

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
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 1/");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 4/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 4/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 6/");
    }

    @Test
    void whenTheBoundariesForNewCardsAreGreaterThanNewCards_ExpectTheDifferenceInSetOfCardsForRepetitionIfPossible() {
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionWith4NewCardsAnd3Reps(cardSetId));
        // Should be lead to all 3 new cards and 4 cards to repeat

        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("new");

        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 1/");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 4/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 4/").doesNotContain("(not in)");
        assertThat(learnSession.getNextCard().getCurrentCard().get().getFront()).contains("rep 6/");
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
