package de.xenadu.learningcards.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LearnSessionConfig {

    private final long cardSetId;
    private LearnSessionId learnSessionId;
    private long userId;
    private boolean spellChecking;
    private boolean onlyRepetition;
    private int numberOfNewCards = 2;
    private int numberOfCardsForRepetition = 7;
    private boolean recentlyLearnedFirst = true;


    public LearnSessionConfig(long cardSetId) {
        this.cardSetId = cardSetId;
    }

    public boolean isRecentlyLearnedFirst() {
        return recentlyLearnedFirst;
    }
}
