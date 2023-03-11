package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.AnswerAuditor;
import de.xenadu.learningcards.domain.AnswerRequest;
import de.xenadu.learningcards.domain.AnswerResult;
import de.xenadu.learningcards.domain.LearnSessionConfig;
import de.xenadu.learningcards.persistence.entities.Card;
import javax.enterprise.context.ApplicationScoped;

import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class WordByWordAnswerAuditor implements AnswerAuditor {
    @Override
    public AnswerResult checkResult(AnswerRequest answerRequest, Card card) {
        List<String> answerWords = getAllTrimmedWords(answerRequest.answer());
        List<String> correctWords = getAllTrimmedWords(card.getBack());

        if (answerWords.size() != correctWords.size()) {
            return wrongAnswer(answerRequest.answer(), card);
        }

        for (int i = 0; i < answerWords.size(); i++) {
            if (!answerWords.get(i).equals(correctWords.get(i))) {
                return wrongAnswer(answerRequest.answer(), card);
            }
        }

        return correctAnswer(answerRequest.answer(), card);
    }

    private AnswerResult wrongAnswer(String answer, Card card) {
        return new AnswerResult(false, card.getBack(), answer);
    }

    private AnswerResult correctAnswer(String answer, Card card) {
        return new AnswerResult(true, card.getBack(), answer);
    }

    private List<String> getAllTrimmedWords(String sentence) {
        String trimmedSentence = sentence.trim();

        String[] words = trimmedSentence.split("\\s+");

        return Arrays.stream(words)
                .map(String::toLowerCase)
                .toList();
    }
}
