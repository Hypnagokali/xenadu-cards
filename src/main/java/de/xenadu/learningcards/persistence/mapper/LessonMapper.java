package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.LessonDto;
import de.xenadu.learningcards.persistence.entities.Lesson;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * Maps a Lesson to a LessonDto and vice versa.
 */
@Mapper(
    componentModel = "cdi",
    uses = { GenericEntityFactory.class },
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    unmappedSourcePolicy = ReportingPolicy.IGNORE
)
public abstract class LessonMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cards", ignore = true)
    public abstract Lesson mapToLesson(LessonDto lessonDto);
}
