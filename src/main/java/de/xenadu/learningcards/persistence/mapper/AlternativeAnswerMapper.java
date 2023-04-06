package de.xenadu.learningcards.persistence.mapper;

import de.xenadu.learningcards.dto.AlternativeAnswerDto;
import de.xenadu.learningcards.persistence.entities.AlternativeAnswer;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for AlternativeAnswerDto.
 */
@Mapper(
    unmappedSourcePolicy = ReportingPolicy.IGNORE,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    componentModel = "cdi"
)
@Slf4j
public abstract class AlternativeAnswerMapper {

    public abstract AlternativeAnswerDto mapToDto(AlternativeAnswer alternativeAnswer);


}
