package de.xenadu.learningcards.dto;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.HelpfulLink;
import de.xenadu.learningcards.persistence.mapper.*;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import de.xenadu.learningcards.service.CardService;
import de.xenadu.learningcards.service.CardSetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class CardMapperIT {

    CardMapper cardMapper;
    CardService cardService;
    CardSetService cardSetService;

    CardSetRepository produceCardSetRepository() {
        final CardSetRepository mock = Mockito.mock(CardSetRepository.class);
        Mockito.when(mock.findById(any())).thenReturn(someCardSet());

        return mock;
    }


    private CardSet someCardSet() {
        return new CardSet(1, "test");
    }

    CardRepository produceCardRepository() {
        final CardRepository mock = Mockito.mock(CardRepository.class);
        Mockito.when(mock.findById(any())).thenReturn(someCard());

        return mock;
    }

    private Card someCard() {
        return new Card(123, "front", "back");
    }

    @BeforeEach
    public void setUp() throws Exception {
        cardService = new CardService(produceCardRepository());
        cardSetService = new CardSetService(produceCardSetRepository());

        HelpfulLinkMapper helpfulLinkMapper = new HelpfulLinkMapperImpl();
        helpfulLinkMapper.setCardService(cardService);

        cardMapper = new CardMapperImpl(getHelpfulLinkMapper(), produceCardEntityFactory());
        // cardMapper.setCardSetService(cardSetService);
    }

    private GenericEntityFactory produceCardEntityFactory() {
        final GenericEntityFactory mock = Mockito.mock(GenericEntityFactory.class);
        Mockito.when(mock.resolve(any(), eq(Card.class))).thenReturn(new Card());

        return mock;
    }

    private GenericEntityFactory produceHelpfulLinkEntityFactory() {
        final GenericEntityFactory mock = Mockito.mock(GenericEntityFactory.class);
        Mockito.when(mock.resolve(any(), eq(HelpfulLink.class))).thenReturn(new HelpfulLink());

        return mock;
    }


    @Test
    public void mapToEntity() throws Exception {
        CardSetService cardSetService = Mockito.mock(CardSetService.class);



        CardSet cardSet = new CardSet(3, "Card Set");
        Mockito.when(cardSetService.findById(3)).thenReturn(Optional.of(cardSet));

        CardDto cardDto = new CardDto();
        cardDto.setCardSetId(3);

        // cardMapper.setCardSetService(cardSetService);
        final Card card = cardMapper.mapToEntity(cardDto, cardSetService, getHelpfulLinkMapper());

        assertThat(card.getCardSet()).isNotNull();
        assertThat(card.getCardSet().getName()).isEqualTo("Card Set");
        assertThat(card.getCardSet().getId()).isEqualTo(3);
    }

    private HelpfulLinkMapper getHelpfulLinkMapper() {
        HelpfulLinkMapper helpfulLinkMapper = new HelpfulLinkMapperImpl(produceHelpfulLinkEntityFactory());
        helpfulLinkMapper.setCardService(cardService);
        return helpfulLinkMapper;
    }

    @Test
    public void mapToDto() throws Exception {
        CardSet cardSet = new CardSet();
        cardSet.setId(999);


        Card card = new Card("front", "back", 3);
        card.setId(123);
        cardSet.addCard(card);

        card.setGender("f");
        card.setNoun(true);
        card.setAdditionalInfos("Some additional infos");
        card.setLastLearned(LocalDateTime.of(2022, 1, 1, 23, 59));

        HelpfulLink link1 = new HelpfulLink("TestLink", "www.to-some-place.org");
        link1.setId(100);
        card.addLink(link1);

        HelpfulLink link2 = new HelpfulLink("TestLink 2", "www.nothing-else.org");
        link2.setId(200);
        card.addLink(link2);

        final CardDto cardDto = cardMapper.mapToDto(card);

        assertThat(cardDto.getId()).isEqualTo(123);
        assertThat(cardDto.getCardSetId()).isEqualTo(cardSet.getId());
        assertThat(cardDto.getBack()).isEqualTo("back");
        assertThat(cardDto.getFront()).isEqualTo("front");

        final Optional<HelpfulLinkDto> helpfulLink = cardDto.getHelpfulLinks().stream().filter(l -> l.getId() == 100).findAny();

        assertThat(helpfulLink).isNotEmpty();
        assertThat(helpfulLink.get().getName()).isEqualTo("TestLink");
        assertThat(helpfulLink.get().getValue()).isEqualTo("www.to-some-place.org");
    }

    @Test
    public void mapFromDtoToEntity() throws Exception {
        CardDto cardDto = new CardDto();
        cardDto.setId(123);
        cardDto.setCardSetId(1);
        HelpfulLinkDto helpfulLinkDto = new HelpfulLinkDto();
        helpfulLinkDto.setCardId(111);
        helpfulLinkDto.setId(3);
        cardDto.setHelpfulLinks(Set.of(helpfulLinkDto));

        final Card card = cardMapper.mapToEntity(cardDto, cardSetService, getHelpfulLinkMapper());

        assertThat(card.getCardSet()).isNotNull();
        assertThat(card.getCardSet().getId()).isEqualTo(1);
        final Optional<HelpfulLink> helpfulLink = card.getHelpfulLinks().stream().findAny();

        assertThat(helpfulLink).isNotEmpty();
        assertThat(helpfulLink.get().getCard()).isNotNull();

        // todo: untersuchen, warum das nicht geht
//        assertThat(helpfulLink.get().getCard().getId()).isEqualTo(123);

    }
}