package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    @Transactional
    public Card createCard(String front, String back) {
        Card card = new Card();
        card.setFront(front);
        card.setBack(back);
        card.setRepetitionState(0);

        cardRepository.persist(card);

        return card;
    }


    public List<Card> findAllByRepState(int repState) {
        return cardRepository.findByRepetitionState(repState);
    }

    @Transactional
    public Card saveCard(Card card) {
        if (card.getId() > 0) {
            cardRepository.getEntityManager().merge(card);
        } else {
            cardRepository.persist(card);
        }


        return card;
    }

    public Card getById(long id) {
        return cardRepository.findById(id);
    }
}
