package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@NoArgsConstructor
public class CardService {

    private CardRepository cardRepository;

    @Inject
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public List<Card> findAllByRepState(long cardSetId, int repState) {
        Map<String, Object> params = new HashMap<>();
        params.put("cardSetId", cardSetId);
        params.put("repState", repState);

        return cardRepository.list("cardSet.id = :cardSetId AND repetitionState = :repState", params);
    }

    @Transactional
    // ToDo: Returned value not needed
    public Card saveCard(Card card) {
        for (HelpfulLink helpfulLink : card.getHelpfulLinks()) {
            helpfulLink.setCard(card);
        }

        if (card.getId() > 0) {
            cardRepository.getEntityManager().merge(card);
        } else {
            cardRepository.persist(card);
        }

        // retrieve persistence information (id from helpfulLink)
        card = cardRepository.findById(card.getId());


        return card;
    }

    @Transactional
    public void deleteCardById(long id) {
        cardRepository.deleteById(id);
    }

    public Card getById(long id) {
        return cardRepository.findById(id);
    }

    public Optional<Card> findById(long cardId) {
        return Optional.ofNullable(cardRepository.findById(cardId));
    }

    public List<Card> findAllByCardSetId(long cardSetId) {
        return cardRepository.list("cardSet.id", cardSetId);
    }
}
