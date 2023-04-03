package de.xenadu.learningcards.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Alternative answer representation for front end.
 */
@Getter
@Setter
public class AlternativeAnswerDto {

    private long id;

    private String answer;

    private boolean backSide;


}
