package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.AnswerAuditor;
import de.xenadu.learningcards.domain.AnswerRequest;
import de.xenadu.learningcards.domain.AnswerResult;
import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import de.xenadu.learningcards.persistence.entities.Card;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Checking answer word by word.
 */
@ApplicationScoped
@RequiredArgsConstructor
public class WordByWordAnswerAuditor implements AnswerAuditor {


    private final CardService cardService;

    private static final String[] specialChars = {
        "!", ".", ",", "-", "_", ";", ":", "?"
    };

    @Override
    @Transactional
    public AnswerResult checkResult(AnswerRequest answerRequest, Card card) {
        List<String> correctAnswers = new ArrayList<>();

        card = cardService.findByIdAndFetchAlternatives(card.getId());


        if (answerRequest.checkBackSide()) {
            correctAnswers.add(card.getBack());
            correctAnswers.addAll(card.getAlternativeAnswers().stream()
                .filter(AlternativeAnswer::isBackSide)
                .map(AlternativeAnswer::getAnswer)
                .toList());
        } else {
            correctAnswers.add(card.getFront());
            correctAnswers.addAll(card.getAlternativeAnswers().stream()
                .filter(AlternativeAnswer::isFrontSide)
                .map(AlternativeAnswer::getAnswer)
                .toList());
        }

        return checkResultAndAlternatives(answerRequest, card, correctAnswers);
    }

    private AnswerResult checkResultAndAlternatives(AnswerRequest answerRequest,
                                                    Card card, List<String> allCorrectAnswers) {
        // ToDo: use a different data structure: Queue
        String answerToCheck = allCorrectAnswers.remove(0);

        List<String> answerWords = getWordsTrimmedAndWithoutSpecialChars(answerRequest.answer());
        List<String> correctWords = getWordsTrimmedAndWithoutSpecialChars(answerToCheck);

        if (answerWords.size() != correctWords.size()) {
            if (allCorrectAnswers.isEmpty()) {
                return getAnswer(answerRequest, card, false);
            }

            return checkResultAndAlternatives(answerRequest, card, allCorrectAnswers);
        }

        for (int i = 0; i < answerWords.size(); i++) {
            if (!answerWords.get(i).equals(correctWords.get(i))) {
                if (allCorrectAnswers.isEmpty()) {
                    return getAnswer(answerRequest, card, false);
                }

                return checkResultAndAlternatives(answerRequest, card, allCorrectAnswers);
            }
        }

        return getAnswer(answerRequest, card, true);
    }

    private AnswerResult getAnswer(AnswerRequest request, Card card, boolean correct) {
        List<String> alternatives;

        if (request.checkBackSide()) {
            alternatives = card.getAlternativeAnswersForBackSide()
                .stream()
                .map(AlternativeAnswer::getAnswer)
                .toList();
        } else {
            alternatives = card.getAlternativeAnswersForFrontSide()
                .stream()
                .map(AlternativeAnswer::getAnswer)
                .toList();
        }

        return new AnswerResult(
            correct,
            card.getBack(),
            request.answer(),
            request.checkBackSide(),
            alternatives
        );
    }


    private List<String> getWordsTrimmedAndWithoutSpecialChars(String sentence) {
        String trimmedSentence = sentence.trim();

        for (int i = 0; i < specialChars.length; i++) {
            trimmedSentence = trimmedSentence.replace(specialChars[i], " ");
        }

        String[] words = trimmedSentence.split("\\s+");

        return Arrays.stream(words)
            .map(String::toLowerCase)
            .toList();
    }
}
