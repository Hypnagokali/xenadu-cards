package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.repositories.CardSetRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional
public class CardSetService {

    @Inject
    CardSetRepository cardSetRepository;

}
