package de.xenadu.learningcards.service;

import de.xenadu.learningcards.exceptions.EntityNotFoundException;
import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.persistence.projections.RepStateCount;
import de.xenadu.learningcards.persistence.repositories.AlternativeAnswerRepository;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import de.xenadu.learningcards.util.RepetitionStateMapping;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import lombok.NoArgsConstructor;

@ApplicationScoped
@NoArgsConstructor
public class CardService {

    private CardRepository cardRepository;
    private AlternativeAnswerRepository alternativeAnswerRepository;

    @Inject
    public CardService(CardRepository cardRepository, AlternativeAnswerRepository alternativeAnswerRepository) {
        this.cardRepository = cardRepository;
        this.alternativeAnswerRepository = alternativeAnswerRepository;
    }

    public List<Card> findAllByRepState(long cardSetId, int repState) {
        Map<String, Object> params = new HashMap<>();
        params.put("cardSetId", cardSetId);
        params.put("repState", repState);

        return cardRepository.list("cardSet.id = :cardSetId AND repetitionState = :repState",
            params);
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

    public List<RepStateCount> test() {
        return cardRepository.find("SELECT 1 as repetitionState, 2 as numberOfCards FROM Card c")
            .project(RepStateCount.class).list();
    }


    public List<Card> findCardsThatAreReadyForRepetitionByRepState(long cardSetId,
                                                                   int repState,
                                                                   boolean recentlyLearnedFirst,
                                                                   int numberOfCards) {

        LocalDateTime readyForRepetition = RepetitionStateMapping.repStateToLocalDateTime(repState);

        return cardRepository.findReadyToLearnCards(
            repState,
            readyForRepetition,
            cardSetId,
            recentlyLearnedFirst,
            numberOfCards
        );

    }


    public List<RepStateCount> getRepStateCounts(long cardSetId) {
        return cardRepository.find(
            "SELECT "
                + "c.repetitionState as repetitionState, count(c.id) as numberOfCards "
                + "FROM Card c " + "WHERE c.cardSet.id = ?1 "
                + "GROUP BY c.repetitionState",
            cardSetId).project(RepStateCount.class).list();
    }

    public List<Card> findNewCards(long cardSetId, int numberOfNewCards) {
        return cardRepository.findAllByRepStateAndCardSetIdFetchHelpfulLinks(
            0,
            cardSetId,
            true,
            numberOfNewCards
        );
    }


    @Transactional
    public void saveAll(Collection<Card> cards) {
        cardRepository.saveAll(cards);
    }

    public Card findByIdAndFetchAlternatives(long cardId) {
        return cardRepository.findByIdAndFetchAlternatives(cardId)
            .orElseThrow(() -> new EntityNotFoundException("Card not found with ID = " + cardId));
    }

    @Transactional
    public void removeAlternativeAnswerById(long cardId, long alternativeId) {
        alternativeAnswerRepository.deleteById(alternativeId);
    }

    public List<Card> findAllByCardSetIdAndLessonId(long cardSetId, long lessonId) {
        return cardRepository.findAllByCardSetIdAndLessonId(cardSetId, lessonId);
    }
}
