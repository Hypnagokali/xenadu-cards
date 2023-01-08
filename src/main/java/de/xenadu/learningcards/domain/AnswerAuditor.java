package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;

public interface AnswerAuditor {


    AnswerResult checkResult(AnswerRequest request, Card card);
}
