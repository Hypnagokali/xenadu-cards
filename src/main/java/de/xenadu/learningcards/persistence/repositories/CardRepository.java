package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.Card;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {
    public List<Card> findByRepetitionState(int repState) {
        return list("repetitionState", repState);
    }
}
