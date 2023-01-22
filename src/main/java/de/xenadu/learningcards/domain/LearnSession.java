package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Getter
public class LearnSession {

    private final LearnSessionId learnSessionId;
    private final LearnSessionConfig config;
    private final AnswerAuditor answerAuditor;

    private final LearnSessionEventCallback learnSessionEventCallback;

    private final Queue<Card> learningCards;
    private final Set<Card> correctlyAnsweredCards = new HashSet<>();
    private final Set<Card> wronglyAnsweredCards = new HashSet<>();

    private final LocalDateTime started;
    private LocalDateTime finished;


    private Card currentCard = null;

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
        started = LocalDateTime.now();
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

    public AnswerResult checkAnswer(AnswerRequest answerRequest, Card card) {
        AnswerResult r = answerAuditor.checkResult(answerRequest, card);
        if (r.isCorrect()) {
            card.nextRepState();
            card.setLastResultWasCorrect(true);
            card.setLastLearned(LocalDateTime.now());
            correctlyAnsweredCards.add(card);
        } else {
            card.resetRepState();
            card.setLastResultWasCorrect(false);
            card.setLastLearned(LocalDateTime.now());
            wronglyAnsweredCards.add(card);
        }

        learnSessionEventCallback.save(card);

        getNextCard();

        return r;
    }

    public void finish() {
        finished = LocalDateTime.now();
        learnSessionEventCallback.finish(this);
    }

    public int getTotalNumberOfCards() {
        return currentCard == null ? 0 : 1 + learningCards.size() + wronglyAnsweredCards.size() + correctlyAnsweredCards.size();
    }

    public int getNumberOfCardsPassed() {
        return wronglyAnsweredCards.size() + correctlyAnsweredCards.size();
    }

    public int numberOfWrongAnswers() {
        return wronglyAnsweredCards.size();
    }

    public int numberOfCorrectAnswers() {
        return correctlyAnsweredCards.size();
    }

    public LearnSessionStatistics getStatistics() {
        if (finished == null) {
            finished = LocalDateTime.now();
        }
        long seconds = ChronoUnit.SECONDS.between(started, finished);
        return new LearnSessionStatistics(numberOfCorrectAnswers(), numberOfWrongAnswers(), seconds);
    }
}
