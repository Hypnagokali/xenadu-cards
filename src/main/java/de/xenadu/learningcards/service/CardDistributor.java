package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;

import java.util.Map;
import java.util.Queue;

public interface CardDistributor {

    /**
     * Takes a number, how many cards the user wants to learn and distribute them
     * to the repetition states
     *
     * @param config number of cards to distribute
     * @return Map with key as repetitionState and the cards which belong to that repState as Queue
     */
    Map<Integer, Queue<Card>> distribute(LearnSessionConfig config);

}
