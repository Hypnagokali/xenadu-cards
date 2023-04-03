package de.xenadu.learningcards.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a later added answer, that is also correct.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AlternativeAnswer extends CreatedByAndTimestampAudit implements AbstractEntity {

    private static final String GENERATOR = "alt_answer_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private long id;

    private String answer;

    private boolean backSide;

    @ManyToOne
    @JoinColumn(name = "card_id")
    private Card card;

    public AlternativeAnswer(String answer, boolean backSide) {
        this.answer = answer;
        this.backSide = backSide;
    }

    @Transient
    public boolean isFrontSide() {
        return !isBackSide();
    }
}
