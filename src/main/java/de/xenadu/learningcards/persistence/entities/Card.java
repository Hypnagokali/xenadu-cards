package de.xenadu.learningcards.persistence.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Card extends PanacheEntityBase {

    @Id
    @GeneratedValue
    private long id;

    private String front = "";
    private String back = "";
    private int repetitionState = 0;

    public Card(String front, String back) {
        this.front = front;
        this.back = back;
    }

    public Card(String front, String back, int repState) {
        this.repetitionState = repState;
        this.front = front;
        this.back = back;
    }
}
