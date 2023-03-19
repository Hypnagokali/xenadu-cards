package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@ApplicationScoped
public class SimpleCardDistributionStrategy implements CardDistributionStrategy {

    private final CardService cardService;


    @Override
    public Map<Integer, Queue<Card>> distribute(LearnSessionConfig config) {
        int numberOfCardsForDistribution = config.getNumberOfCardsForRepetition();

        Map<Integer, Queue<Card>> repStateCardsMapTemp = new TreeMap<>();
        Map<Integer, Queue<Card>> repStateCardsMapResult = new TreeMap<>();
        // step1: For each repState get the total number of cards available, with upper boundary numberOfCardsForDistribution
        IntStream.rangeClosed(1, 8).forEach(repState -> {
            repStateCardsMapResult.put(repState, new LinkedList<>());
            repStateCardsMapTemp.put(repState, getCardsForState(config, numberOfCardsForDistribution, repState));
        });

        // step2: distribute the given number of cards to the repStates and return
        // i) get distributen 3:1
        int numberOfFreshestCards = (int) Math.round(numberOfCardsForDistribution * 0.75);
        int numberOfOlderCards = numberOfCardsForDistribution - numberOfFreshestCards;

        // ii) check if number of wanted cards exceeds available cards
        int restOfFirstThree = 0;
        int numberOfAllCardsOfTheFirstThreeRepStates = IntStream.range(1, 4)
                .map(repState -> repStateCardsMapTemp.get(repState).size())
                .reduce(0, Integer::sum);

        if (numberOfAllCardsOfTheFirstThreeRepStates < numberOfFreshestCards) {
            numberOfFreshestCards = numberOfAllCardsOfTheFirstThreeRepStates;
        } else {
            restOfFirstThree = numberOfAllCardsOfTheFirstThreeRepStates - numberOfFreshestCards;
        }

        int cardsLeft = 0;
        int numberOfAllCardsOfTheLastFourRepStates = IntStream.range(4, 8)
                .map(repState -> repStateCardsMapTemp.get(repState).size())
                .reduce(0, Integer::sum);

        if (numberOfAllCardsOfTheLastFourRepStates < numberOfOlderCards) {
            cardsLeft = numberOfOlderCards - numberOfAllCardsOfTheLastFourRepStates;
            numberOfOlderCards = numberOfAllCardsOfTheLastFourRepStates;
        }

        if (cardsLeft > 0) {
            if (cardsLeft > restOfFirstThree) {
                cardsLeft = restOfFirstThree;
            }

            // put the cards that remained on the first stack of cards
            numberOfFreshestCards += cardsLeft;
        }

        distributeCards(
                1, 3,
                repStateCardsMapTemp, repStateCardsMapResult,
                numberOfFreshestCards
        );
        distributeCards(
                4, 7,
                repStateCardsMapTemp, repStateCardsMapResult,
                numberOfOlderCards
        );


        return repStateCardsMapResult;
    }

    private void distributeCards(
            int firstRepState,
            int lastRepStateInclusive,
            Map<Integer, Queue<Card>> repStateCardsMapTemp,
            Map<Integer, Queue<Card>> repStateCardsMapResult,
            int numberOfCardsToDistribute) {

        while (numberOfCardsToDistribute > 0) {
            for (int i = firstRepState; i <= lastRepStateInclusive; i++) {
                final Queue<Card> cardQueue = repStateCardsMapTemp.get(i);
                if (!cardQueue.isEmpty()) {
                    repStateCardsMapResult.get(i).add(cardQueue.remove());
                    numberOfCardsToDistribute--;
                }
            }
        }
    }

    private Queue<Card> getCardsForState(LearnSessionConfig config, int numberOfCards, int repState) {
        final List<Card> cards = cardService.findCardsThatAreReadyForRepetitionByRepState(
                config.getCardSetId(),
                repState,
                config.isRecentlyLearnedFirst(),
                numberOfCards
        );

        return new LinkedList<>(cards);
    }
}
