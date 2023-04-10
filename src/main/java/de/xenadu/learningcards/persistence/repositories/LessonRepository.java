package de.xenadu.learningcards.persistence.repositories;

import de.xenadu.learningcards.persistence.entities.Lesson;
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
                SELECT l FROM Lesson l
                LEFT JOIN FETCH l.cards
                WHERE l.id = ?1
                """, Lesson.class)
            .setParameter(1, id)
            .getSingleResult());
    }
}
