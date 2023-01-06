package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;
import lombok.Getter;

import java.util.Optional;
import java.util.Queue;
import java.util.UUID;


@Getter
public class LearnSession {

    private final LearnSessionId learnSessionId;
    private final LearnSessionConfig config;
    private final AnswerAuditor answerAuditor;

    private final LearnSessionEventCallback learnSessionEventCallback;

    private final Queue<Card> learningCards;


    private Card currentCard = null;
    private int totalNumberOfCards;
    private int numberOfCardsPassed;

    @SuppressWarnings("CdiInjectionPointsInspection")
    public LearnSession(LearnSessionConfig learnSessionConfig,
                        Queue<Card> learningCards,
                        AnswerAuditor answerAuditor,
                        LearnSessionEventCallback learnSessionEventCallback
    ) {
        learnSessionId = new LearnSessionId(UUID.randomUUID().toString());
        this.learningCards = learningCards;
        this.answerAuditor = answerAuditor;
        this.learnSessionEventCallback = learnSessionEventCallback;
        config = learnSessionConfig;
        config.setLearnSessionId(learnSessionId);
        this.totalNumberOfCards = learningCards.size();
        this.numberOfCardsPassed = 0;
    }

    public Optional<Card> getCurrentCard() {
        return Optional.ofNullable(currentCard);
    }

    public LearnSession getNextCard() {
        if (learningCards.size() > 0) {
            currentCard = learningCards.remove();
        } else {
            currentCard = null;
        }

        return this;
    }

    public AnswerResult checkAnswer(String answer, Card card) {
        AnswerResult r = answerAuditor.checkResult(answer, card);
        if (r.isCorrect()) {
            card.nextRepState();
            card.setLastResultWasCorrect(true);
        } else {
            card.prevRepState();
            card.setLastResultWasCorrect(false);
        }

        numberOfCardsPassed++;

        learnSessionEventCallback.save(card);

        getNextCard();

        return r;
    }

    public void finish() {
        // cards should already be saved.
        // Todo: generate or publish statistics?
        learnSessionEventCallback.finish(this);
    }

    public int getTotalNumberOfCards() {
        return totalNumberOfCards;
    }

    public int getNumberOfCardsPassed() {
        return numberOfCardsPassed;
    }
}
