package de.xenadu.learningcards.dto;

import java.util.List;

/**
 * DTO for type Lesson.
 *
 * @param id ID of entity.
 * @param name Name of lesson.
 * @param cardSetId Belonging cardSe.
 * @param cards Number of cards in lesson.
 */
public record LessonDto(long id, String name, long cardSetId, int cards){}
