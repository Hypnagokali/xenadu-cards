package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.projections.RepStateCount;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CardServiceTest {

    @Inject
    CardService cardService;

    @Inject
    CardSetService cardSetService;

    @Test
    public void getRepState1And5Test() throws Exception {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime recentlyLearned = LocalDateTime.now();
        LocalDateTime notSoRecentlyLearned = LocalDateTime.now().minusDays(1);
        LocalDateTime rep5RepetitionDateTime = LocalDateTime.now().minusMonths(1);
        LocalDateTime notInRep5RepetitionDateTime = LocalDateTime.now().minusMonths(1).plusDays(2);


        cardSet.addCard(new Card("new 1", "neu 1", 1, recentlyLearned));
        cardSet.addCard(new Card("new 2", "neu 2", 1, notSoRecentlyLearned));
        cardSet.addCard(new Card("new 3", "neu 3", 5, rep5RepetitionDateTime.minusDays(1)));
        cardSet.addCard(new Card("new 4", "neu 4", 5, rep5RepetitionDateTime));
        cardSet.addCard(new Card("new 5", "neu 5", 5, notInRep5RepetitionDateTime));

        cardSetService.save(cardSet, userInfo);

        List<Card> cards = cardService
                .findCardsThatAreReadyForRepetitionByRepState(cardSet.getId(), 1, true);
        // expect card new 2, new 1 is too fresh for repetition
        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).getFront()).isEqualTo("new 2");

        cards = cardService.findCardsThatAreReadyForRepetitionByRepState(
                cardSet.getId(),
                5,
                true
        );

        // assert card "new 4" first, because, it was the last learned card in repState 5
        assertThat(cards).hasSize(2);
        assertThat(cards.get(0).getFront()).isEqualTo("new 4");

        cards = cardService.findCardsThatAreReadyForRepetitionByRepState(
                cardSet.getId(),
                5,
                false
        );

        assertThat(cards).hasSize(2);
        assertThat(cards.get(0).getFront()).isEqualTo("new 3");
    }

    @Test
    public void queryRepStateCounts() throws Exception {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime recentlyLearned = LocalDateTime.now();
        LocalDateTime someTimeHasPassedSinceLearning = LocalDateTime.now().minusDays(1);


        cardSet.addCard(new Card("new 1", "neu 1", 1, recentlyLearned));
        cardSet.addCard(new Card("new 2", "neu 2", 1, recentlyLearned));
        cardSet.addCard(new Card("new 3", "neu 3", 3, someTimeHasPassedSinceLearning));
        cardSet.addCard(new Card("new 4", "neu 4", 4, someTimeHasPassedSinceLearning));
        cardSet.addCard(new Card("new 5", "neu 5", 4, recentlyLearned));
        cardSet.addCard(new Card("old 1", "alt 1", 5, someTimeHasPassedSinceLearning));

        cardSetService.save(cardSet, userInfo);

        final List<RepStateCount> newCards = cardService.getRepStateCounts(cardSet.getId());

        System.out.println();
    }

    @Test
    public void queryingCardsTest() throws Exception {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime recentlyLearned = LocalDateTime.now();
        LocalDateTime someTimeHasPassedSinceLearning = LocalDateTime.now().minusDays(1);


        cardSet.addCard(new Card("new 1", "neu 1", 0, recentlyLearned));
        cardSet.addCard(new Card("new 2", "neu 2", 0, recentlyLearned));
        cardSet.addCard(new Card("new 3", "neu 3", 0, someTimeHasPassedSinceLearning));
        cardSet.addCard(new Card("new 4", "neu 4", 0, someTimeHasPassedSinceLearning));
        cardSet.addCard(new Card("new 5", "neu 5", 0, recentlyLearned));
        cardSet.addCard(new Card("old 1", "alt 1", 1, someTimeHasPassedSinceLearning));

        cardSetService.save(cardSet, userInfo);
        final List<Card> newCards = cardService.findNewCards(cardSet.getId(), 2);

        final Comparator<Card> comparing = Comparator.comparing(Card::getFront);
        newCards.sort(comparing);

        assertThat(newCards).hasSize(2);
        assertThat(newCards.get(0).getFront()).isEqualTo("new 3");
        assertThat(newCards.get(1).getFront()).isEqualTo("new 4");
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
