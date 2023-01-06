package de.xenadu.learningcards.domain;

public record CardSetInfos(int totalNumberOfNewCards,
                           int totalNumberOfCardsForRepetition,
                           int numberOfFails,
                           int numberOfSuccesses)
{}
