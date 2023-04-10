package de.xenadu.learningcards.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class LessonServiceIT {

    @Inject
    LessonService lessonService;

    @Inject
    CardSetRepository cardSetRepository;

    @Inject
    EntityManager em;

    @Inject
    GetUserInfo getUserInfo;

    CardSet testCardSet;

    @BeforeAll
    static void beforeAll() {
        GetUserInfo getUserInfoMock = Mockito.mock(GetUserInfo.class);
        Mockito.when(getUserInfoMock.authenticatedUser())
            .thenReturn(testUserInfo());
        QuarkusMock.installMockForType(getUserInfoMock, GetUserInfo.class);
    }

    @BeforeEach
    @Transactional
    public void setUp() {
        testCardSet = new CardSet(0, "Test CardSet");
        Card card1 = new Card("card1", "Karte 1");
        Card card2 = new Card("card2", "Karte 2");
        Card card3 = new Card("card3", "Karte 3");
        testCardSet.addCard(card1);
        testCardSet.addCard(card2);
        testCardSet.addCard(card3);
        cardSetRepository.persist(testCardSet);
    }

    private static UserInfo testUserInfo() {
        return new UserInfo(99999, "ittest@example.org", "it", "test");
    }

    @Test
    void createALessonTest() {
        Lesson lesson = new Lesson("Lesson 1");
        lessonService.save(lesson);

        assertThat(lesson.getId()).isGreaterThan(0);
    }

    @Test
    void addCardsToLessonTest() {
        Lesson createdLesson = createNewLession();
        Card card2 = testCardSet.getCards().stream()
            .filter(c -> c.getFront().equals("card2"))
            .findAny()
            .get();

        createdLesson.addCard(card2);
        lessonService.save(createdLesson);
        em.clear();

        Lesson lesson = lessonService.findByIdWithCards(createdLesson.getId());

        assertThat(lesson.getCards()).hasSize(1);
    }

    private Lesson createNewLession() {
        Lesson lesson = new Lesson("Lesson 2");
        lesson.setCardSet(testCardSet);
        lessonService.save(lesson);

        return lesson;
    }
}
