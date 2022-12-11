package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleCardDistributorTest {

    CardDistributor cardDistributor;
    LearnSessionConfig config;
    CardService cardService;

    @BeforeEach
    public void setUp() throws Exception {
        cardService = Mockito.mock(CardService.class);
        config = new LearnSessionConfig(1);
        cardDistributor = new SimpleCardDistributor(cardService);
    }

    @Test
    public void whenTheLastFourRepsAreSmallerThantWantedCards_DistributeThemOnTheFirstThree() throws Exception {
        config.setNumberOfNewCards(0);
        config.setNumberOfCardsForRepetition(4);

        // When there are 4 cards in the first 3 states and none in the last 4
        prepareMocks();

        // And we want 4 cards to be distributed
        final Map<Integer, Queue<Card>> distribute = cardDistributor.distribute(config);

        // Expect 2 Cards in repState 3 and 1 Card in repState 1 and 2
        final Queue<Card> cardsIn1 = distribute.get(1);
        final Queue<Card> cardsIn2 = distribute.get(2);
        final Queue<Card> cardsIn3 = distribute.get(3);

        assertThat(cardsIn3).hasSize(2);
        assertThat(cardsIn1).hasSize(1);
        assertThat(cardsIn2).hasSize(1);
    }

    private void prepareMocks() {
        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                1,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(List.of(new Card("card 1", "card 1")));

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                2,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(List.of(new Card("card 2", "card 2")));

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                3,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(List.of(new Card("card 3", "card 3"), new Card("card 4", "card 4")));

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                4,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(new ArrayList<>());

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                5,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(new ArrayList<>());

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                6,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(new ArrayList<>());

        Mockito.when(cardService.findCardsThatAreReadyForRepetitionByRepState(
                1,
                7,
                true,
                config.getNumberOfCardsForRepetition()
        )).thenReturn(new ArrayList<>());


    }
}