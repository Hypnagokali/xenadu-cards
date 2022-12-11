package de.xenadu.learningcards.persistence.projections;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@RegisterForReflection
@Getter
public class RepStateCount {

    public int repetitionState;
    public long numberOfCards;

    public RepStateCount(int repetitionState, long numberOfCards) {
        this.repetitionState = repetitionState;
        this.numberOfCards = numberOfCards;
    }
}
