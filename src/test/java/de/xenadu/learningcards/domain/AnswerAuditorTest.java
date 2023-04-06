package de.xenadu.learningcards.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.WordByWordAnswerAuditor;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AnswerAuditorTest {

    CardService cardService;

    @BeforeEach
    void setUp() {
        cardService = Mockito.mock(CardService.class);

        Mockito.when(cardService.findByIdAndFetchAlternatives(any()))
            .thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void whenTheAnswerContainsWhitespacesButIsCorrectAsAWhole_ExpectResultIsCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor(cardService);
        Card card = new Card("Meine Mutter trink saft", "Моя мама пьёт сок");
        String myAnswer = "  Моя    Мама пьёт сок   ";

        AnswerResult answerResult = auditor.checkResult(new AnswerRequest(myAnswer, true), card);

        assertThat(answerResult.isCorrect()).isTrue();
    }

    @Test
    void whenTheAnswerisNotCorrect_ExpectResultIsNotCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor(cardService);
        Card card = new Card("Meine Mutter trink saft", "Моя мама пьёт сок");
        String myAnswer = "Моя Мама пёт сок";

        AnswerResult answerResult = auditor.checkResult(new AnswerRequest(myAnswer, true), card);

        assertThat(answerResult.isCorrect()).isFalse();
    }
}