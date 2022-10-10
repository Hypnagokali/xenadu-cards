package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CardSetMapperTest {

    CardSetMapper cardSetMapper = Mappers.getMapper(CardSetMapper.class);

    @Test
    public void givenDto_whenMaps_thenCorrect() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setName("test");
        cardSet.setUserId(123);
        cardSet.setId(2);
        cardSet.setCards(twoCards());

        final CardSetDto cardSetDto = cardSetMapper.mapToDto(cardSet);

        assertThat(cardSetDto.getId()).isEqualTo(2);
        assertThat(cardSetDto.getUserId()).isEqualTo(123);
        assertThat(cardSetDto.getName()).isEqualTo("test");
        assertThat(cardSetDto.getNumberOfCards()).isEqualTo(2);

    }

    private Set<Card> twoCards() {
        return Set.of(new Card("front1", "back1", 2),
                new Card("front2", "back2", 0));
    }
}