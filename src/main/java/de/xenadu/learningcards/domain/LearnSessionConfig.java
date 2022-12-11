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
    private int numberOfNewCards;
    private int numberOfCardsForRepetition;
    private boolean recentlyLearnedFirst = true;


    public LearnSessionConfig(long cardSetId) {
        this.cardSetId = cardSetId;
    }

    public boolean isRecentlyLearnedFirst() {
        return recentlyLearnedFirst;
    }
}
