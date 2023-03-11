package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.LearnSession;
import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


@SuppressWarnings("ALL")
@Disabled
class LearnSessionManagerTest {

    LearnSessionManager learnSessionManager;
    private CardService cardService;

    @BeforeEach
    public void setUp() throws Exception {
        cardService = Mockito.mock(CardService.class);
        learnSessionManager = new LearnSessionManager(cardService, new SimpleCardDistributionStrategy(cardService), new WordByWordAnswerAuditor());
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

        final LearnSession learnSession = learnSessionManager
                .startNewLearnSession(learnSessionConfig);

        Card cardNew1 = learnSession.getNextCard().getCurrentCard().get();
        Card cardNew2 = learnSession.getNextCard().getCurrentCard().get();
        Card cardOld = learnSession.getNextCard().getCurrentCard().get();

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
}