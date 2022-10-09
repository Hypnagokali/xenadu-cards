package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Card;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class CardServiceTest {

    @Inject
    CardService cardService;

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
