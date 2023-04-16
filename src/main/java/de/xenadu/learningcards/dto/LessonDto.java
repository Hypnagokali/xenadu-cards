package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.persistence.entities.Lesson;
import java.util.List;

/**
 * DTO for type Lesson.
 *
 * @param id ID of entity.
 * @param name Name of lesson.
 * @param cardSetId Belonging cardSe.
 * @param cards Number of cards in lesson.
 */
public record LessonDto(long id, String name, long cardSetId, int cards) implements AbstractDto {

    public LessonDto(Lesson lesson, long cardSetId) {
        this(lesson.getId(), lesson.getName(), cardSetId, lesson.getCards().size());
    }

    @Override
    public long getId() {
        return id();
    }
}
