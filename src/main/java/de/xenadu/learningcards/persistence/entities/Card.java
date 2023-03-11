package de.xenadu.learningcards.persistence.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card extends CreatedByAndTimestampAudit implements AbstractEntity {

    private static final String GENERATOR = "card_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private long id;

    private String front = "";
    private String back = "";
    private boolean noun;


    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private boolean lastResultWasCorrect = false;

    @Column(columnDefinition = "CHARACTER VARYING (12) DEFAULT 'n'")
    private String gender = "n";

    @Column(columnDefinition = "TEXT default ''")
    private String additionalInfos = "";

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HelpfulLink> helpfulLinks = new LinkedHashSet<>();

    private int repetitionState = 0;

    @Column(columnDefinition = "TEXT DEFAULT ''")
    private String hint = "";
    private LocalDateTime lastLearned = LocalDateTime.of(1800, 1, 1, 0, 0);

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    private CardSet cardSet;

    public Card(String front, String back) {
        this.front = front;
        this.back = back;
    }

    public Card(long id, String front, String back) {
        this.id = id;
        this.front = front;
        this.back = back;
    }

    public Card(String front, String back, int repState) {
        this.repetitionState = repState;
        this.front = front;
        this.back = back;
    }

    public Card(String front, String back, int repState, LocalDateTime lastLearned) {
        this.repetitionState = repState;
        this.front = front;
        this.back = back;
        this.lastLearned = lastLearned;
    }

    public Card(String front, String back, int repState, LocalDateTime lastLearned, boolean lastAnswerWasCorrect) {
        this.repetitionState = repState;
        this.front = front;
        this.back = back;
        this.lastLearned = lastLearned;
        this.lastResultWasCorrect = lastAnswerWasCorrect;
    }

    public void addLink(HelpfulLink helpfulLink) {
        helpfulLink.setCard(this);
        this.helpfulLinks.add(helpfulLink);
    }

    public void nextRepState() {
        if (lastResultWasCorrect) {
            // for now, it is okay, when the card gets to an undefined repState.
            repetitionState++;
        } else {
            lastResultWasCorrect = true;
        }
    }

    public void resetRepState() {
        if (repetitionState > 0) {
            repetitionState = 1;
        } else {
            repetitionState = 0;
        }
    }
}
