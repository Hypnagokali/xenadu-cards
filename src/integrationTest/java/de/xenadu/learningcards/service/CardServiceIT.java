package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.projections.RepStateCount;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import io.quarkus.test.junit.QuarkusTest;
import javax.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
@Transactional
public class CardServiceIT {

    @Inject
    CardService cardService;

    @Inject
    CardSetService cardSetService;

    @Inject
    CardSetRepository cardSetRepository;
    @Inject
    CardRepository cardRepository;

    CardSet cardSet;


    @BeforeEach
    void setUp() {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime recentlyLearned = LocalDateTime.now();
        LocalDateTime rep6RepetitionDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime notInRep6RepetitionDateTime = LocalDateTime.now().minusMonths(1).plusDays(2);

        cardSet.addCard(new Card("new 1", "neu 1", 1, recentlyLearned));
        cardSet.addCard(new Card("new 3", "neu 3", 6, rep6RepetitionDateTime.minusDays(1)));
        cardSet.addCard(new Card("new 4", "neu 4", 6, rep6RepetitionDateTime));
        cardSet.addCard(new Card("new 5", "neu 5", 6, notInRep6RepetitionDateTime));

        cardSetService.save(cardSet, userInfo);
    }

    @AfterEach
    public void tearDown() {
        cardRepository.deleteAll();
        cardSetRepository.deleteAll();
    }

    @Test
    void repState6Test_whenRecentlyLearnedFirstIsTrue_ExpectTheLatestCardFirst() {
        List<Card> cards = cardService.findCardsThatAreReadyForRepetitionByRepState(
            cardSet.getId(),
            6,
            true,
            99
        );

        // assert card "new 4" first, because it was the last learned card in that repState
        assertThat(cards).hasSize(2);
        assertThat(cards.get(0).getFront()).isEqualTo("new 4");

    }

    @Test
    void repState6Test_whenRecentlyLearnedFirstIsFalse_ExpectTheFirstLearnedCardFirst() {
        List<Card> cards = cardService.findCardsThatAreReadyForRepetitionByRepState(
            cardSet.getId(),
            6,
            false,
            99
        );

        // assert card "new 3" first, because it was the first learned card in that repState
        assertThat(cards).hasSize(2);
        assertThat(cards.get(0).getFront()).isEqualTo("new 3");

    }

    @Test
    public void repState1Test() throws Exception {
        List<Card> cards = cardService
                .findCardsThatAreReadyForRepetitionByRepState(
                    cardSet.getId(),
                    1,
                    true,
                    99
                );
        // expect card new 1
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getFront()).isEqualTo("new 1");
    }

    @Test
    public void retrieveOnlyNewCardsTest() throws Exception {
        final CardSet cardSet = cardSetWithTwoCardsWithRepState0();

        final List<Card> newCards = cardService.findNewCards(cardSet.getId(), 2);

        sortCardsByFrontText(newCards);

        assertThat(newCards).hasSize(2);
        assertThat(newCards.get(0).getFront()).isEqualTo("new 3");
        assertThat(newCards.get(1).getFront()).isEqualTo("new 4");
    }

    private static void sortCardsByFrontText(List<Card> newCards) {
        final Comparator<Card> comparing = Comparator.comparing(Card::getFront);
        newCards.sort(comparing);
    }

    private CardSet cardSetWithTwoCardsWithRepState0() {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime recentlyLearned = LocalDateTime.now();
        LocalDateTime someTimeHasPassedSinceLearning = LocalDateTime.now().minusDays(1);


        cardSet.addCard(new Card("new 1", "neu 1", 1, recentlyLearned));
        cardSet.addCard(new Card("new 2", "neu 2", 1, recentlyLearned));
        cardSet.addCard(new Card("new 3", "neu 3", 0, recentlyLearned));
        cardSet.addCard(new Card("new 4", "neu 4", 0, null));
        cardSet.addCard(new Card("new 5", "neu 5", 2, recentlyLearned));
        cardSet.addCard(new Card("old 1", "alt 1", 3, someTimeHasPassedSinceLearning));

        cardSetService.save(cardSet, userInfo);

        return cardSet;
    }

    @Test
    public void addingACardTest() throws Exception {
        Card card = new Card("front", "back", 2);
        final Card savedCard = cardService.saveCard(card);

        assertThat(savedCard.getId()).isGreaterThan(0);
    }

    @Test
    public void creatingNewCardAndManipulatingItTest() throws Exception {
        Card card = new Card("front", "back", 2);
        final Card savedCard = cardService.saveCard(card);

        Card loadedCard = cardService.getById(card.getId());

        assertThat(loadedCard.getId()).isGreaterThan(0);
        assertThat(loadedCard.getBack()).isEqualTo("back");
        assertThat(loadedCard.getFront()).isEqualTo("front");
        assertThat(loadedCard.getRepetitionState()).isEqualTo(2);

        loadedCard.setFront("new Front");
        loadedCard.setRepetitionState(5);
        cardService.saveCard(loadedCard);
        assertThat(loadedCard.getId()).isEqualTo(savedCard.getId());

        loadedCard = cardService.getById(loadedCard.getId());

        assertThat(loadedCard.getFront()).isEqualTo("new Front");
        assertThat(loadedCard.getRepetitionState()).isEqualTo(5);

    }
}
