package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import de.xenadu.learningcards.persistence.entities.Card;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.Getter;



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

    /**
     * Get current card. Is empty in the beginning.
     *
     * @return The current card, if present.
     */
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
        return answerAuditor.checkResult(answerRequest, card);
    }

    public void finish() {
        finished = LocalDateTime.now();
        learnSessionEventCallback.finish(this);
    }

    public int getTotalNumberOfCards() {
        return currentCard == null
            ? 0
            : 1 + learningCards.size()
            + wronglyAnsweredCards.size()
            + correctlyAnsweredCards.size();
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
        return new LearnSessionStatistics(
            numberOfCorrectAnswers(),
            numberOfWrongAnswers(),
            seconds
        );
    }

    /**
     * Add a new alternative answer to the card.
     *
     * @param answerResult The previous result.
     * @param card Current card.
     * @return Corrected answer result.
     */
    public AnswerResult addNewAnswer(AnswerResult answerResult, Card card) {
        List<String> alternatives = new ArrayList<>();
        if (answerResult.isBackSide()) {
            card.addAlternativeToBack(answerResult.givenAnswer());
            alternatives = card.getAlternativeAnswers()
                .stream()
                .filter(AlternativeAnswer::isBackSide)
                .map(AlternativeAnswer::getAnswer)
                .toList();
        } else {
            card.addAlternativeToFront(answerResult.givenAnswer());
            alternatives = card.getAlternativeAnswers()
                .stream()
                .filter(AlternativeAnswer::isFrontSide)
                .map(AlternativeAnswer::getAnswer)
                .toList();
        }

        return new AnswerResult(
            true,
            answerResult.expectedAnswer(),
            answerResult.givenAnswer(),
            answerResult.isBackSide(),
            alternatives
        );
    }

    /**
     * Apply the AnswerResult to the card.
     *
     * @param answerResult Result.
     * @param card Current card.
     */
    public void commit(AnswerResult answerResult, Card card) {
        if (answerResult.isCorrect()) {
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
    }
}
