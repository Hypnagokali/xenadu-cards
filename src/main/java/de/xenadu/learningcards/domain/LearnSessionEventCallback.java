package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;

import java.util.Collection;

public interface LearnSessionEventCallback {

    void save(Card card);
    void saveAll(Collection<Card> cards);

    void finish(LearnSession learnSession);
}
