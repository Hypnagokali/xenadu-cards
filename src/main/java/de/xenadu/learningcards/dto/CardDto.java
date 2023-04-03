package de.xenadu.learningcards.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class CardDto implements AbstractDto {

    private long id;
    private String front = "";
    private String back = "";
    private int repetitionState = 0;
    private String lastLearned = "1800-01-01 12:00";
    private long cardSetId = 0;

    private String hint = "";
    private boolean noun;
    private String gender = null;
    private String additionalInfos = "";

    private Set<AlternativeAnswerDto> alternativeAnswers = new LinkedHashSet<>();
    private Set<HelpfulLinkDto> helpfulLinks = new LinkedHashSet<>();
}
