package de.xenadu.learningcards.persistence.entities;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lesson Entity.
 * A Lesson can have multiple cards. A card can be assigned to many lessons.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Lesson extends CreatedByAndTimestampAudit implements AbstractEntity {

    private static final String GENERATOR = "lesson_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "card_set_id")
    private CardSet cardSet;

    @ManyToMany
    @JoinTable(
        joinColumns = @JoinColumn(name = "lesson_id"),
        inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards = new LinkedHashSet<>();

    public Lesson(String name) {
        this.name = name;
    }

    public void addCard(Card card) {
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
        card.getLessons().remove(this);
    }
}
