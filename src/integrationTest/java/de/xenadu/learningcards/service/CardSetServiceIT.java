package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.CardSetInfos;
import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CardSetServiceIT {

    @Inject
    CardSetService cardSetService;
    private CardSet testCardSet;

    @BeforeEach
    void setUp() {
        testCardSet = new CardSet();
        testCardSet.setName("test");
        testCardSet.setUser(testUser());

        Card card = new Card("new", "neu", 0);
        Card cardOld = new Card("old 1", "alt 1", 1);
        Card cardOld2 = new Card("old 2", "alt 2", 6);
        testCardSet.addAll(Set.of(card, cardOld, cardOld2));

        cardSetService.save(testCardSet, testUser());
    }

    private static UserInfo testUser() {
        final UserInfo userInfo = new UserInfo();
        userInfo.setEmail("test@example.org");
        userInfo.setId(123);

        return userInfo;
    }

    @Test
    void loadCardSetInfos_ExpectCorrectRepresentationOfTheData() {
        CardSetInfos cardSetInfos = cardSetService.getCardSetInfos(testCardSet.getId());

        assertThat(cardSetInfos.totalNumberOfNewCards()).isEqualTo(1);
        assertThat(cardSetInfos.totalNumberOfCardsForRepetition()).isEqualTo(2);
    }

    @Test
    public void createANewCardSetTest() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setName("English");
        cardSet.setUser(testUser());

        final CardSet savedCardSet = cardSetService.save(cardSet, testUser());

        assertThat(savedCardSet.getId()).isGreaterThan(0);
        assertThat(savedCardSet.getUserId()).isEqualTo(123L);
    }

    @Test
    public void loadExistingCard_andUpdateCardTest() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setName("Test Card Set");
        cardSet.setUser(testUser());
        final CardSet savedCardSet = cardSetService.save(cardSet, testUser());

        Set<CardSet> cardSets = cardSetService.findAllByUserId(123);

        final Optional<CardSet> loadedCardSet = cardSets.stream().filter(cs -> cs.getId() == savedCardSet.getId()).findAny();

        assertThat(loadedCardSet).isNotEmpty();
        assertThat(loadedCardSet.get().getName()).isEqualTo("Test Card Set");

        loadedCardSet.get().setName("Edited");
        final CardSet updatedCardSet = cardSetService.save(loadedCardSet.get(), testUser());

        assertThat(updatedCardSet.getName()).isEqualTo("Edited");
    }
}
