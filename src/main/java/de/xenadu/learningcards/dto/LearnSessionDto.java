package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.domain.AnswerResult;

public record LearnSessionDto(String learnSessionId,
                              CardDto currentCard,
                              int numberOfCardsPassed,
                              int totalNumberOfCards,
                              boolean spellChecking,
                              AnswerResult answerResult
                              ) {
}
