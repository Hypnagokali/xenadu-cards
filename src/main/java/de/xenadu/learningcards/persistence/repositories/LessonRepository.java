package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.Lesson;
import java.util.List;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

/**
 * CRUD Repo for lesson entity.
 */
@ApplicationScoped
@Transactional
public class LessonRepository extends CrudRepository<Lesson> {

    /**
     * Find Lesson by ID and eager fetching cards.
     *
     * @param id Lesson ID.
     *
     * @return Optional with Lesson if found.
     */
    public Optional<Lesson> findByIdFetchCards(long id) {
        return Optional.ofNullable(getEntityManager()
            .createQuery("""
                SELECT DISTINCT l FROM Lesson l
                LEFT JOIN FETCH l.cards
                WHERE l.id = ?1
                """, Lesson.class)
            .setParameter(1, id)
            .getSingleResult());
    }

    /**
     * Find all lessons for specific user.
     *
     * @param userId ID of user.
     *
     * @return List of {@link Lesson}.
     */
    public List<Lesson> findAllByUserId(long userId) {
        return find("""
            SELECT l FROM Lesson l
            WHERE l.cardSet.userId = ?1
            """, userId)
            .list();
    }

    /**
     * Find all Lessons by CardSet ID. With all cards eager fetched.
     *
     * @param cardSetId CardSet ID.
     *
     * @return List of lessons.
     */
    public List<Lesson> findAllByCardSetIdFetchCards(long cardSetId) {
        return getEntityManager().createQuery("""
                SELECT DISTINCT l FROM Lesson l
                LEFT JOIN FETCH l.cards
                WHERE l.cardSet.id = ?1
                """, Lesson.class)
            .setParameter(1, cardSetId)
            .getResultList();
    }

    /**
     * Find all lessons by a single card.
     *
     * @param cardId ID of card.
     *
     * @return List of lessons.
     */
    public List<Lesson> findByCardId(long cardId) {
        return find("""
            SELECT DISTINCT l FROM Lesson l
            LEFT JOIN FETCH l.cards
            LEFT JOIN l.cards c
            WHERE c.id = ?1
            """, cardId).list();
    }
}
