package de.xenadu.learningcards.service;

import de.xenadu.learningcards.persistence.entities.Lesson;

/**
 * CRUD service for lesson objects.
 */
public interface LessonService {

    void save(Lesson lesson);

    Lesson findById(long id);

    Lesson findByIdWithCards(long id);
}
