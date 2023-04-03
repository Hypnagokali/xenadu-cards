package de.xenadu.learningcards.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import de.xenadu.learningcards.domain.AnswerRequest;
import de.xenadu.learningcards.domain.AnswerResult;
import de.xenadu.learningcards.domain.LearnSession;
import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@SuppressWarnings("ALL")
class LearnSessionManagerTest {

    LearnSessionManager learnSessionManager;
    CardDistributionStrategy cardDistributionStrategy;
    Card testCard;
    private CardService cardService;

    @NotNull
    private Card answerCardWrongAndAddNewAnswerAsAlternative(LearnSession learnSession,
                                                                    String alternative) {
        testCard = learnSession.getNextCard().getCurrentCard().get();
        mockCardService();

        AnswerRequest answerOfUser =
            new AnswerRequest(alternative, true);

        AnswerResult answerResult = learnSession.checkAnswer(answerOfUser, testCard);
        answerResult = learnSession.addNewAnswer(answerResult, testCard);
        learnSession.commit(answerResult, testCard);
        return testCard;
    }

    @BeforeEach
    public void setUp() throws Exception {
        cardService = Mockito.mock(CardService.class);

        cardDistributionStrategy = Mockito.mock(CardDistributionStrategy.class);
        learnSessionManager = new LearnSessionManager(cardService, cardDistributionStrategy,
            new WordByWordAnswerAuditor(cardService));
    }

    @Test
    void whenCheckBackSideIsFalse_ExpectCardFrontWillBeChecked() {
        LearnSessionConfig learnSessionConfig = configureLearnSessionWithTwoNewCards();
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);
        testCard = learnSession.getNextCard().getCurrentCard().get();

        mockCardService();

        AnswerResult answerResult =
            learnSession.checkAnswer(new AnswerRequest("new 1", false), testCard);

        assertThat(answerResult.isCorrect()).isTrue();
    }

    @Test
    void whenAnsweredWrongButClaimingToBeCorrect_ExpectAnswerIsAddedAndCardIsMarkedAsCorrect() {
        LearnSessionConfig learnSessionConfig = configureLearnSessionWithTwoNewCards();
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);

        Card card = answerCardWrongAndAddNewAnswerAsAlternative(learnSession,
            "This is an alternative answer");

        assertThat(card.getRepetitionState()).isEqualTo(1);
    }

    @Test
    void whenTheAnswerIsAnAlternative_ExpectThisAnswerIsCorrect() {
        LearnSessionConfig learnSessionConfig = configureLearnSessionWithTwoNewCards();
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);
        Card card = answerCardWrongAndAddNewAnswerAsAlternative(learnSession,
            "This is an alternative answer");

        AnswerResult result =
            learnSession.checkAnswer(
                new AnswerRequest("This is an alternative answer", true),
                card
            );

        assertThat(result.isCorrect()).isTrue();
    }

    @Test
    void whenThereAreAlternatives_ExpectTheyExistInAnswerRequest() {
        LearnSessionConfig learnSessionConfig = configureLearnSessionWithTwoNewCards();
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);
        Card card = answerCardWrongAndAddNewAnswerAsAlternative(learnSession,
            "This is an alternative answer");

        AnswerResult result =
            learnSession.checkAnswer(
                new AnswerRequest("This is another and wrong answer", true),
                card
            );

        assertThat(result.alternatives()).hasSize(1);
    }

    @Test
    void whenAnswerContainsSpecialChars_ExpectLearnSessionIgnoresThem() {
        LearnSessionConfig learnSessionConfig = configureLearnSessionWithTwoNewCards();
        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);

        testCard = learnSession.getNextCard().getCurrentCard().get();

        mockCardService();

        AnswerResult result =
            learnSession.checkAnswer(
                new AnswerRequest("neu-1?", true),
                testCard
            );

        assertThat(result.isCorrect()).isTrue();
    }

    @NotNull
    private LearnSessionConfig configureLearnSessionWithTwoNewCards() {
        LearnSessionConfig learnSessionConfig = new LearnSessionConfig(1);
        learnSessionConfig.setNumberOfNewCards(2);
        learnSessionConfig.setNumberOfCardsForRepetition(0);

        Mockito.when(cardService.findNewCards(1, 2))
            .thenReturn(new ArrayList<>(newCards()));
        return learnSessionConfig;
    }

    @Test
    public void whenCreatingLearnManager_ExpectLearnSessionId() throws Exception {
        LearnSessionConfig learnSessionConfig = new LearnSessionConfig(1);

        LearnSession learnSession = learnSessionManager.startNewLearnSession(learnSessionConfig);

        assertThat(learnSession.getLearnSessionId()).isNotNull();

        Optional<LearnSession> retrievedLearnSession = learnSessionManager
            .getLearnSession(learnSession.getLearnSessionId());

        assertThat(retrievedLearnSession).isNotEmpty();
        assertThat(retrievedLearnSession.get().getLearnSessionId())
            .isEqualTo(learnSession.getLearnSessionId());
    }

    @Test
    public void whenInitConfigWith2Cards_ExpectRetrieve2CardsFromSession()
        throws Exception {
        final LearnSessionConfig learnSessionConfig = new LearnSessionConfig(1);
        learnSessionConfig.setNumberOfNewCards(2);
        learnSessionConfig.setNumberOfCardsForRepetition(2);

        Mockito.when(cardService.findNewCards(1, 2))
            .thenReturn(new ArrayList<>(newCards()));

        Mockito.when(cardDistributionStrategy.distribute(learnSessionConfig))
            .thenReturn(oldCardsDistributed());

        final LearnSession learnSession = learnSessionManager
            .startNewLearnSession(learnSessionConfig);

        Card cardNew1 = learnSession.getNextCard().getCurrentCard().get();
        Card cardNew2 = learnSession.getNextCard().getCurrentCard().get();
        Card cardOld = learnSession.getNextCard().getCurrentCard().get();

    }

    private Map<Integer, Queue<Card>> oldCardsDistributed() {
        Set<Card> cards = oldCards();
        return new HashMap<>() {{
            put(1, new LinkedList<>(cards));
        }};
    }

    private Set<Card> newCards() {
        final CardSet cardSet = new CardSet(1, "TestCards");

        cardSet.addCard(new Card("new 1", "neu 1", 0));
        cardSet.addCard(new Card("new 2", "neu 2", 0));

        return cardSet.getCards();
    }

    private Set<Card> oldCards() {
        final CardSet cardSet = new CardSet(1, "TestCards");

//        cardSet.addCard(new Card("new 1", "neu 1", 0));
//        cardSet.addCard(new Card("new 2", "neu 2", 0));
        cardSet.addCard(new Card("old 1", "alt 1", 1));

        return cardSet.getCards();
    }

    private void mockCardService() {
        Mockito.when(cardService.findByIdAndFetchAlternatives(any(Long.class)))
            .thenReturn(testCard);
    }

}