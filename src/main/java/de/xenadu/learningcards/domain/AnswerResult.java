package de.xenadu.learningcards.domain;

import java.util.ArrayList;
import java.util.List;

public record AnswerResult(boolean isCorrect,
                           String expectedAnswer,
                           String givenAnswer,
                           boolean isBackSide,
                           List<String> alternatives) {

    public AnswerResult(boolean isCorrect, String expectedAnswer, String givenAnswer) {
        this(isCorrect, expectedAnswer, givenAnswer, true, new ArrayList<>());
    }

}
