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

    Card testCard;

    @BeforeEach
    void setUp() {
        cardService = Mockito.mock(CardService.class);
        testCard = new Card("Meine Mutter trink saft", "Моя мама пьёт сок");

        Mockito.when(cardService.findByIdAndFetchAlternatives(any(Long.class)))
            .thenReturn(testCard);
    }

    @Test
    void whenTheAnswerContainsWhitespacesButIsCorrectAsAWhole_ExpectResultIsCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor(cardService);

        String myAnswer = "  Моя    Мама пьёт сок   ";

        AnswerResult answerResult = auditor.checkResult(new AnswerRequest(myAnswer, true), testCard);

        assertThat(answerResult.isCorrect()).isTrue();
    }

    @Test
    void whenTheAnswerisNotCorrect_ExpectResultIsNotCorrect() {
        AnswerAuditor auditor = new WordByWordAnswerAuditor(cardService);
        String myAnswer = "Моя Мама пёт сок";

        AnswerResult answerResult = auditor.checkResult(new AnswerRequest(myAnswer, true), testCard);

        assertThat(answerResult.isCorrect()).isFalse();
    }
}