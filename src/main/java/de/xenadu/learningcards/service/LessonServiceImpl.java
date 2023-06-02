package de.xenadu.learningcards.service;

import de.xenadu.learningcards.exceptions.EntityNotFoundException;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import de.xenadu.learningcards.persistence.repositories.LessonRepository;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * {@inheritDoc}
 **/
@ApplicationScoped
@Transactional
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CardRepository cardRepository;

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

    @Override
    public List<Lesson> findAllByCardId(long cardId) {
        return lessonRepository.findByCardIdAndFetchCards(cardId);
    }

    @Override
    public void assignCardToLesson(Card card, Lesson lesson) {
        Lesson loadedLesson = lessonRepository.findByIdFetchCards(lesson.getId())
            .orElseThrow(() -> notFound(lesson.getId()));

        Card loadedCard = cardRepository.findByIdAndFetchLessons(card.getId())
            .orElseThrow(() -> new EntityNotFoundException("Card not found. ID = " + card.getId()));

        loadedLesson.addCard(loadedCard);

        lessonRepository.save(loadedLesson);

    }

    @Override
    public void removeCardFromLesson(Card card, Lesson lesson) {
        Lesson loadedLesson = lessonRepository.findByIdFetchCards(lesson.getId())
            .orElseThrow(() -> notFound(lesson.getId()));

        Card loadedCard = cardRepository.findByIdAndFetchLessons(card.getId())
            .orElseThrow(() -> new EntityNotFoundException("Card not found. ID = " + card.getId()));

        loadedLesson.removeCard(loadedCard);

        lessonRepository.save(loadedLesson);
    }


    private EntityNotFoundException notFound(long id) {
        return new EntityNotFoundException("Lesson not found. ID = " + id);
    }
}
