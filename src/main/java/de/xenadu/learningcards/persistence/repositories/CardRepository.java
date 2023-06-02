package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.Card;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import javax.persistence.EntityManager;

@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {



    public List<Card> findByRepetitionState(int repState) {
        return list("repetitionState", repState);
    }


    public List<Card> findReadyToLearnCards(int repState,
                                            LocalDateTime readyToLearnDateTime,
                                            long cardSetId,
                                            boolean recentlyLearnedFirst,
                                            int numberOfCards
    ) {
        final PanacheQuery<Card> query = find(
            "SELECT c FROM Card c "
                + "WHERE c.repetitionState = ?1 "
                + "AND c.cardSet.id = ?2 "
                + "AND c.lastLearned <= ?3",
            repState,
            cardSetId,
            readyToLearnDateTime);

        return getOrderedCardsWithHelpfulLinks(recentlyLearnedFirst, numberOfCards, query);
    }

    private List<Card> getOrderedCardsWithHelpfulLinks(boolean recentlyLearnedFirst, int numberOfCards,
                                                       PanacheQuery<Card> query) {
        if (numberOfCards > 0) {
            List<Long> cardsWithLimitedSize =
                query.range(0, numberOfCards - 1).list().stream()
                    .map(Card::getId)
                    .toList();

            List<Card> cards = fetchHelpfulLinksAndAnswers(cardsWithLimitedSize);
            // The second query would destroy the order of the first. Therefor the ordering
            // will be done here - manually:
            if (recentlyLearnedFirst) {
                cards.sort(Comparator.comparing(Card::getLastLearned).reversed());
            } else {
                cards.sort(Comparator.comparing(Card::getLastLearned));
            }

            cards.sort(Comparator.comparing(Card::isLastResultWasCorrect));
            return cards;
        }

        // If number of cards is 0 for whatever reason, just return an empty list.
        return new ArrayList<>();
    }

    public List<Card> findAllByRepStateAndCardSetIdFetchHelpfulLinks(int repState,
                                                                     long cardSetId,
                                                                     boolean recentlyLearnedFirst,
                                                                     int numberOfNewCards
    ) {
        final PanacheQuery<Card> cardPanacheQuery = find(
            "SELECT c FROM Card c "
                + "WHERE c.repetitionState = ?1 "
                + "AND c.cardSet.id = ?2 ",
                0, cardSetId);

        return getOrderedCardsWithHelpfulLinks(recentlyLearnedFirst, numberOfNewCards, cardPanacheQuery);
    }

    private List<Card> fetchHelpfulLinksAndAnswers(List<Long> cardIds) {
        EntityManager em = getEntityManager();
        return em.createQuery("""
            SELECT DISTINCT c FROM Card c
            LEFT JOIN FETCH c.helpfulLinks
            LEFT JOIN FETCH c.alternativeAnswers
            WHERE c.id IN (?1)
            """, Card.class)
            .setParameter(1, cardIds)
            .getResultList();
    }

    public void saveAll(Collection<Card> cards) {
        for (Card card : cards) {
            getEntityManager().merge(card);
        }
    }

    public Optional<Card> findByIdAndFetchAlternatives(long id) {
        return Optional.ofNullable(getEntityManager().createQuery("""
                SELECT DISTINCT c FROM Card c
                    LEFT JOIN FETCH c.alternativeAnswers
                WHERE c.id = ?1
                """, Card.class)
            .setParameter(1, id)
            .getSingleResult());
    }

    public List<Card> findAllByCardSetIdAndLessonId(long cardSetId, long lessonId) {
        return find("""
                    SELECT DISTINCT c FROM Card c
                    LEFT JOIN c.lessons l
                    WHERE c.cardSet.id = ?1 AND l.id = ?2
                """,
            cardSetId,
            lessonId
        ).list();
    }

    /**
     * Retrieves a Card with all lessons associated with.
     *
     * @param cardId ID of Card.
     * @return Optional with a card if found, empty else.
     */
    public Optional<Card> findByIdAndFetchLessons(long cardId) {
        return find("""
                SELECT DISTINCT c FROM Card c
                LEFT JOIN FETCH c.lessons
                WHERE c.id = ?1
            """, cardId).firstResultOptional();
    }
}
