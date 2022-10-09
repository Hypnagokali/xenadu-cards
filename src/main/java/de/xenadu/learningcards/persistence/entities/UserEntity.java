package de.xenadu.learningcards.persistence.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private String userName;

    @OneToMany(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private Set<CardSet> cardSets = new LinkedHashSet<>();

}
