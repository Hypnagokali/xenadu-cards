package de.xenadu.learningcards.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR)
    private long id;

    private String name;



    public Lesson(String name) {
        this.name = name;
    }
}
