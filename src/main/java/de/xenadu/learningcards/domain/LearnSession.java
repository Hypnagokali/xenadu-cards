package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;
import lombok.Getter;

import java.util.Queue;
import java.util.UUID;


@Getter
public class LearnSession {

    private final LearnSessionId learnSessionId;
    private final LearnSessionConfig config;

    private final Queue<Card> learningCards;

    @SuppressWarnings("CdiInjectionPointsInspection")
    public LearnSession(LearnSessionConfig learnSessionConfig, Queue<Card> learningCards) {
        learnSessionId = new LearnSessionId(UUID.randomUUID().toString());
        this.learningCards = learningCards;
        config = learnSessionConfig;
        config.setLearnSessionId(learnSessionId);
    }


    public Card getNextCard() {
        return learningCards.remove();
    }
}
