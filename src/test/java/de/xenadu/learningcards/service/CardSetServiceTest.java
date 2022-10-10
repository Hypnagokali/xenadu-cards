package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.CardSet;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.inject.Inject;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CardSetServiceTest {

    @Inject
    CardSetService cardSetService;

    private static UserInfo testUser() {
        final UserInfo userInfo = new UserInfo();
        userInfo.setEmail("test@example.org");
        userInfo.setId(123);

        return userInfo;
    }

    @Test
    public void createANewCardSetTest() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setName("English");

        final CardSet savedCardSet = cardSetService.save(cardSet, testUser());

        assertThat(savedCardSet.getId()).isGreaterThan(0);
        assertThat(savedCardSet.getUserId()).isEqualTo(123L);
    }

    @Test
    public void loadExistingCard_andUpdateCardTest() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setName("Test Card Set");
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
