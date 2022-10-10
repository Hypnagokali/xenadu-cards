package de.xenadu.learningcards.persistence.entities;

import de.xenadu.learningcards.domain.UserInfo;
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
    private long userId;

    private String name = "";


    @OneToMany(mappedBy = "cardSet", cascade = CascadeType.ALL)
    private Set<Card> cards = new LinkedHashSet<>();


    public void setUser(UserInfo user) {
        this.userId = user.getId();
    }

}
