package de.xenadu.learningcards.domain;

public record AnswerResult(boolean isCorrect, String expectedAnswer, String givenAnswer) {}
