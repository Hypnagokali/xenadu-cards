package de.xenadu.learningcards.persistence.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HelpfulLink implements AbstractEntity {

    private static final String GENERATOR = "helpful_link_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private long id;

    private String name;

    @Column(name = "link_value", columnDefinition = "TEXT DEFAULT ''")
    private String value;

    @ManyToOne
    @JoinColumn(name = "card_id")
    @JsonIgnore
    private Card card;

    public HelpfulLink(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
