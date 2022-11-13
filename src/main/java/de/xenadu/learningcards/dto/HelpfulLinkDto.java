package de.xenadu.learningcards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HelpfulLinkDto implements AbstractDto {

    private long id;
    private String name;
    private String value;
    private long cardId;

}
