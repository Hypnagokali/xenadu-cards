package de.xenadu.learningcards.service;

import de.xenadu.learningcards.exceptions.EntityNotFoundException;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.repositories.LessonRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 *
 **/
@ApplicationScoped
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public void save(Lesson lesson) {
        lessonRepository.save(lesson);
    }

    @Override
    public Lesson findByIdOrThrow(long id) {
        return lessonRepository.findByIdOptional(id)
            .orElseThrow(() -> notFound(id));
    }

    @Override
    public Lesson findByIdWithCards(long id) {
        return lessonRepository.findByIdFetchCards(id)
            .orElseThrow(() -> notFound(id));
    }

    @Override
    public List<Lesson> findAllByUserId(long userId) {
        return lessonRepository.findAllByUserId(userId);
    }

    @Override
    public List<Lesson> findAllByCardSetId(long cardSetId) {
        return lessonRepository.findAllByCardSetIdFetchCards(cardSetId);
    }

    @Override
    public void deleteById(long id) {
        lessonRepository.deleteById(id);
    }


    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Lesson not found. ID = " + id);
    }
}
