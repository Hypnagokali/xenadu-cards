package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.domain.AnswerResult;
import de.xenadu.learningcards.domain.LearnSessionStatistics;

public record LearnSessionDto(String learnSessionId,
                              long cardSetId,
                              CardDto currentCard,
                              int numberOfCardsPassed,
                              int totalNumberOfCards,
                              boolean spellChecking,
                              AnswerResult answerResult,
                              LearnSessionStatistics statistics
                              ) {
}
