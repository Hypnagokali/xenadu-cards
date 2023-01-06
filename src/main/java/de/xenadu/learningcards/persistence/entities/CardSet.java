package de.xenadu.learningcards.persistence.entities;

import de.xenadu.learningcards.domain.UserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CardSet extends CreatedByAndTimestampAudit implements AbstractEntity {

    private static final String GENERATOR = "card_set_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = GENERATOR)
    @SequenceGenerator(name = GENERATOR, sequenceName = GENERATOR, allocationSize = 1)
    private long id;

    private long userId;

    private String name = "";


    @OneToMany(
            mappedBy = "cardSet",
            cascade = CascadeType.ALL, // { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH},
            orphanRemoval = true
    )
    private Set<Card> cards = new LinkedHashSet<>();

    public CardSet(long id, String name) {
        this.id = id;
        this.name = name;
    }


    public void setUser(UserInfo user) {
        this.userId = user.getId();
    }

    public void addCard(Card card) {
        card.setCardSet(this);
        this.cards.add(card);
    }

    public void addAll(Set<Card> cardSet) {
        for (Card card : cardSet) {
            card.setCardSet(this);
            this.cards.add(card);
        }
    }
}
