package de.xenadu.learningcards.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class CardSet extends CreatedByAndTimestampAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name = "";

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private Set<Card> cards = new LinkedHashSet<>();

}
