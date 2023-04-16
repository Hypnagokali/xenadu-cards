package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Lesson;
import java.util.List;

/**
 * CRUD service for lesson objects.
 */
public interface LessonService {

    void save(Lesson lesson);

    Lesson findByIdOrThrow(long id);

    Lesson findByIdWithCards(long id);

    List<Lesson> findAllByUserId(long userId);

    List<Lesson> findAllByCardSetId(long cardSetId);

    void deleteById(long id);
}
