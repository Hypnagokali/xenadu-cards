package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDateTime;

@QuarkusTest
public class LearnSessionManagerTest {

    @Inject
    CardService cardService;

    @Inject
    CardSetService cardSetService;

    @Test
    public void generateLearningSetTest() throws Exception {
        final UserInfo userInfo = new UserInfo(2, "test@test", "test", "test");

        final CardSet cardSet = new CardSet(0, "TestCards");
        cardSet.setUser(userInfo);

        LocalDateTime rep1DateTime = LocalDateTime.now().minusHours(2);
        LocalDateTime rep3DateTime = LocalDateTime.now().minusDays(2);
        LocalDateTime notInRep3DateTime = LocalDateTime.now().minusDays(2).plusHours(2);
        LocalDateTime rep5DateTime = LocalDateTime.now().minusMonths(1);


        cardSet.addCard(new Card("new 1", "neu 1"));
        cardSet.addCard(new Card("new 2", "neu 2"));
        cardSet.addCard(new Card("new 3", "neu 3"));
        cardSet.addCard(new Card("rep 1/1", "rep 1", 1, rep1DateTime));
        cardSet.addCard(new Card("rep 3/1", "rep 3", 3, rep3DateTime));
        cardSet.addCard(new Card("rep 3/2 (not in)", "not in rep 3", 3, notInRep3DateTime));
        cardSet.addCard(new Card("rep 3/3", "rep 3", 3, rep5DateTime));
        cardSet.addCard(new Card("rep 5/1", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/2", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/3", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/4", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/5", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/6", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/7", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/8", "rep 5", 5, rep5DateTime));
        cardSet.addCard(new Card("rep 5/9", "rep 5", 5, rep5DateTime));

        cardSetService.save(cardSet, userInfo);

        // Want to learn 7 cards, 2 of them must be new
    }
}
