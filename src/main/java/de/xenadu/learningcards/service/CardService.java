package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.persistence.projections.RepStateCount;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;

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

    public List<RepStateCount> test() {
        return cardRepository.find("SELECT 1 as repetitionState, 2 as numberOfCards FROM Card c")
                .project(RepStateCount.class)
                .list();
    }

    public List<Card> findCardsThatAreReadyForRepetitionByRepState(
            long cardSetId,
            int repState,
            boolean recentlyLearnedFirst,
            int numberOfCards) {
        String orderDirection = recentlyLearnedFirst ? "DESC" : "ASC";

        LocalDateTime readyForRepetition = hardCodedMapping(repState);

        final PanacheQuery<Card> query = cardRepository.find(
                "SELECT c FROM Card c " +
                        "WHERE c.repetitionState = ?1 " +
                        "AND c.cardSet.id = ?2 " +
                        "AND (c.lastLearned <= ?3 OR c.lastResultWasCorrect = false)" +
                        "ORDER BY c.lastLearned " + orderDirection,
                repState,
                cardSetId,
                readyForRepetition);

        if (numberOfCards > 0) {
            List<Long> cardsWithLimitedSize = query.range(0, numberOfCards - 1)
                    .list().stream()
                    .map(Card::getId)
                    .toList();

            return fetchHelpfulLinks(cardsWithLimitedSize);
        }

        // If number of cards is 0 for whatever reason, just return an empty list.
        return new ArrayList<>();
        // return fetchHelpfulLinks(query.list().stream().map(Card::getId).toList());
    }

    public List<Card> findCardsThatAreReadyForRepetitionByRepState(
            long cardSetId,
            int repState,
            boolean recentlyLearnedFirst) {
        return findCardsThatAreReadyForRepetitionByRepState(cardSetId, repState, recentlyLearnedFirst, 0);
    }

    private LocalDateTime hardCodedMapping(int repState) {
        // ToDo: Hardcoded
        final LocalDateTime now = LocalDateTime.now();
        switch (repState) {
            case 0:
                return now;
            case 1:
                return now.minusHours(1);
            case 2:
                return now.minusHours(12);
            case 3:
                return now.minusDays(2);
            case 4:
                return now.minusDays(8);
            case 5:
                return now.minusMonths(1);
            case 6:
                return now.minusMonths(2);
            case 7:
                return now.minusMonths(6);
        }

        throw new IllegalStateException(
                String.format("Dieser repState wurde nicht gefunden. RepState = %d", repState));
    }

    public List<RepStateCount> getRepStateCounts(long cardSetId) {
        return cardRepository.find("SELECT " +
                        "c.repetitionState as repetitionState, count(c.id) as numberOfCards " +
                        "FROM Card c " +
                        "WHERE c.cardSet.id = ?1 " +
                        "GROUP BY c.repetitionState", cardSetId)
                .project(RepStateCount.class).list();
    }

    public List<Card> findNewCards(long cardSetId, int numberOfNewCards) {
        final PanacheQuery<Card> cardPanacheQuery = cardRepository.find(
                "SELECT c FROM Card c " +
                        "WHERE c.repetitionState = ?1 " +
                        "AND c.cardSet.id = ?2 " +
                        "ORDER BY c.lastLearned ASC", 0, cardSetId);

        List<Long> cardIds = cardPanacheQuery.range(0, numberOfNewCards - 1).list()
                .stream().map(Card::getId).toList();

        return fetchHelpfulLinks(cardIds);
    }

    private List<Card> fetchHelpfulLinks(List<Long> cardIds) {
        EntityManager em = cardRepository.getEntityManager();
        return em.createQuery("""
                        SELECT DISTINCT c FROM Card c
                        LEFT JOIN FETCH c.helpfulLinks
                        WHERE c.id IN (?1)
                        """, Card.class)
                .setParameter(1, cardIds)
                .getResultList();
    }

    @Transactional
    public void saveAll(Collection<Card> cards) {
        for (Card card : cards) {
            cardRepository.getEntityManager().merge(card);
        }
    }
}
