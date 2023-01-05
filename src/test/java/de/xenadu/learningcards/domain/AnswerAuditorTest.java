package de.xenadu.learningcards.domain;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.WordByWordAnswerAuditor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AnswerAuditorTest {

    @Test
    void whenTheAnswerContainsWhitespacesButIsCorrectAsAWhole_ExpectResultIsCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor();
        Card card = new Card("Meine Mutter trink saft", "Моя мама пьёт сок");
        String myAnswer = "  Моя    Мама пьёт сок   ";

        AnswerResult answerResult = auditor.checkResult(myAnswer, card);

        assertThat(answerResult.isCorrect()).isTrue();
    }

    @Test
    void whenTheAnswerisNotCorrect_ExpectResultIsNotCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor();
        Card card = new Card("Meine Mutter trink saft", "Моя мама пьёт сок");
        String myAnswer = "Моя Мама пёт сок";

        AnswerResult answerResult = auditor.checkResult(myAnswer, card);

        assertThat(answerResult.isCorrect()).isFalse();
    }
}