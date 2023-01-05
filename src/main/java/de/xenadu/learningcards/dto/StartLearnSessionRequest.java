package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.domain.LearnSessionId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StartLearnSessionRequest {
    private boolean spellChecking;
    private boolean onlyRepetition;
    private int numberOfNewCards;
    private int numberOfCardsForRepetition;
    private boolean recentlyLearnedFirst = true;

}
