package de.xenadu.learningcards.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardSetDto implements AbstractDto{

    private long id;
    private long userId;
    private String name;
    private int numberOfCards;

}
