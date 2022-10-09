package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.CardSet;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CardSetRepository implements PanacheRepository<CardSet> {
}
