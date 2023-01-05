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

    private final Queue<Card> learningCards;


    private Card currentCard = null;

    @SuppressWarnings("CdiInjectionPointsInspection")
    public LearnSession(LearnSessionConfig learnSessionConfig, Queue<Card> learningCards, AnswerAuditor answerAuditor) {
        learnSessionId = new LearnSessionId(UUID.randomUUID().toString());
        this.learningCards = learningCards;
        this.answerAuditor = answerAuditor;
        config = learnSessionConfig;
        config.setLearnSessionId(learnSessionId);
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
        // ToDo: manipulate card an save
        return r;
    }
}
