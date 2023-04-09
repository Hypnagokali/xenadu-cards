package de.xenadu.learningcards.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "card")
    private Set<AlternativeAnswer> alternativeAnswers = new LinkedHashSet<>();

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
    private LocalDateTime lastLearned = initDate();
    @ManyToOne
    @JoinColumn(name = "card_set_id")
    @JsonIgnore
    private CardSet cardSet;

    public Card(String front, String back) {
        this.front = front;
        this.back = back;
    }

    public Card(String front, String back, int repState) {
        this(front, back);
        this.repetitionState = repState;
    }

    public Card(long id, String front, String back) {
        this(front, back);
        this.id = id;
    }

    public Card(String front, String back, int repState, LocalDateTime lastLearned) {
        this(front, back, repState);
        if (lastLearned == null) {
            this.lastLearned = initDate();
        } else {
            this.lastLearned = lastLearned;
        }
    }

    public Card(String front, String back, int repState, LocalDateTime lastLearned,
                boolean lastAnswerWasCorrect) {
        this(front, back, repState, lastLearned);
        this.lastResultWasCorrect = lastAnswerWasCorrect;
    }

    private static LocalDateTime initDate() {
        return LocalDateTime.of(1800, 1, 1, 0, 0);
    }

    public void addLink(HelpfulLink helpfulLink) {
        helpfulLink.setCard(this);
        this.helpfulLinks.add(helpfulLink);
    }

    public void nextRepState() {
        repetitionState++;
    }

    public void resetRepState() {
        // repState = 0 is only for new cards
        // reState = 1 means: card already seen
        repetitionState = 1;
    }

    /**
     * Adds an alternative answer to the back side.
     *
     * @param alternative Alternative answer.
     */
    public void addAlternativeToBack(String alternative) {
        AlternativeAnswer alternativeAnswer = new AlternativeAnswer(alternative, true);
        this.alternativeAnswers.add(alternativeAnswer);
        alternativeAnswer.setCard(this);
    }

    public void addAlternativeToFront(String alternative) {
        AlternativeAnswer alternativeAnswer = new AlternativeAnswer(alternative, false);
        this.alternativeAnswers.add(alternativeAnswer);
        alternativeAnswer.setCard(this);
    }

    @Transient
    public List<AlternativeAnswer> getAlternativeAnswersForBackSide() {
        return alternativeAnswers
            .stream()
            .filter(AlternativeAnswer::isBackSide)
            .toList();
    }

    @Transient
    public List<AlternativeAnswer> getAlternativeAnswersForFrontSide() {
        return alternativeAnswers
            .stream()
            .filter(AlternativeAnswer::isFrontSide)
            .toList();
    }
}
