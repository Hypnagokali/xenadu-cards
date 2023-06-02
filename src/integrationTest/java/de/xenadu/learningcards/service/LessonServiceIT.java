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
import java.util.Optional;
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
    CardService cardService;

    @Inject
    EntityManager em;

    @Inject
    GetUserInfo getUserInfo;

    CardSet testCardSet;
    CardSet anotherUsersCardSet;

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
        testCardSet.setUser(testUserInfo());
        Card card1 = new Card("card1", "Karte 1");
        Card card2 = new Card("card2", "Karte 2");
        Card card3 = new Card("card3", "Karte 3");
        testCardSet.addCard(card1);
        testCardSet.addCard(card2);
        testCardSet.addCard(card3);
        cardSetRepository.persist(testCardSet);

        // Another users CardSet:
        anotherUsersCardSet = new CardSet(0, "Another CardSet");
        anotherUsersCardSet.setUser(anotherUserInfo());
        cardSetRepository.persist(anotherUsersCardSet);
    }

    private UserInfo anotherUserInfo() {
        return new UserInfo(88888, "another@example.org", "an", "other");
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
        Lesson createdLesson = createNewLesson();
        Card card2 = testCardSet.getCards().stream()
            .filter(c -> c.getFront().equals("card2"))
            .findAny()
            .get();

        lessonService.assignCardToLesson(card2, createdLesson);
        em.clear();

        Lesson lesson = lessonService.findByIdWithCards(createdLesson.getId());

        assertThat(lesson.getCards()).hasSize(1)
            .anyMatch(c -> c.getFront().equals("card2"));
    }

    @Test
    void removeCardFromLessonTest() {
        Lesson createdLesson = createNewLesson();
        Card card2 = testCardSet.getCards().stream()
            .filter(c -> c.getFront().equals("card2"))
            .findAny()
            .get();

        lessonService.assignCardToLesson(card2, createdLesson);
        em.clear();

        lessonService.removeCardFromLesson(card2, createdLesson);

        Lesson lesson = lessonService.findByIdWithCards(createdLesson.getId());

        assertThat(lesson.getCards()).hasSize(0);
    }

    @Test
    void findLessonsByCardId() {
        Lesson createdLesson = createNewLesson();
        Lesson anotherLesson = createAnotherLesson();

        Card card2 = testCardSet.getCards().stream()
            .filter(c -> c.getFront().equals("card2"))
            .findAny()
            .get();

        createdLesson.addCard(card2);
        anotherLesson.addCard(card2);

        lessonService.save(createdLesson);
        lessonService.save(anotherLesson);

        em.clear();

        List<Lesson> allByCardId = lessonService.findAllByCardId(card2.getId());

        assertThat(allByCardId).hasSize(2);
    }

    @Test
    void findAllLessonsForAuthenticatedUserTest() {
        createNewLesson();
        createAnotherLesson();
        em.clear();

        List<Lesson> lessons = lessonService.findAllByUserId(testUserInfo().getId());

        assertThat(lessons).anyMatch(l -> l.getName().equals("Lesson 2"));
    }

    @Test
    void whenDeletingLesson_ExpectCardsAreNotAffected() {
        Lesson newLesson = createNewLesson();
        Card card2 = testCardSet.getCards().stream()
            .filter(c -> c.getFront().equals("card2"))
            .findAny()
            .get();

        newLesson.addCard(card2);
        lessonService.save(newLesson);
        em.clear();

        lessonService.deleteById(newLesson.getId());

        Optional<Card> cardOfLesson = cardService.findById(card2.getId());

        assertThat(cardOfLesson).isNotEmpty();
    }

    private Lesson createAnotherLesson() {
        Lesson lesson = new Lesson("Lesson 3");
        lesson.setCardSet(anotherUsersCardSet);
        lessonService.save(lesson);

        return lesson;
    }

    private Lesson createNewLesson() {
        Lesson lesson = new Lesson("Lesson 2");
        lesson.setCardSet(testCardSet);
        lessonService.save(lesson);

        return lesson;
    }
}
