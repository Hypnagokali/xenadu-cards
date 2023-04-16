package de.xenadu.learningcards.controller;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.entities.Lesson;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import de.xenadu.learningcards.service.GetUserInfo;
import de.xenadu.learningcards.service.LessonService;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


@QuarkusTest
@TestHTTPEndpoint(LessonController.class)
public class LessonControllerIT {

    @Inject
    CardSetRepository cardSetRepository;

    @Inject
    LessonService lessonService;

    @Inject
    EntityManager em;

    @Inject
    GetUserInfo getUserInfo;

    CardSet testCardSet;
    Lesson lesson1;
    Lesson lesson2;

    Card testCardNotAssignedToLesson1;


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

        testCardNotAssignedToLesson1 = card2;

        lesson1 = new Lesson("Lesson 1");
        lesson1.setCardSet(testCardSet);
        lesson1.addCard(card1);

        lesson2 = new Lesson("Lesson 2");
        lesson2.setCardSet(testCardSet);
        lesson2.addCard(card2);
        lesson2.addCard(card3);

        lessonService.save(lesson1);
        lessonService.save(lesson2);
    }

    @Test
    void findAllByUserAndCardSetTest() {
        given()
            .pathParam("cardSetId", testCardSet.getId())
            .log().ifValidationFails()
            .get()
            .then()
            .statusCode(200)
            .body("size()", is(2));

    }

    @Test
    void createNewLessonTest() {
        String lessonJson = """
            {
                "id": 0,
                "name": "a new lesson"
            }
            """;

        given()
            .pathParam("cardSetId", testCardSet.getId())
            .log().ifValidationFails()
            .body(lessonJson)
            .contentType(ContentType.JSON)
            .post()
            .then()
            .statusCode(200)
            .body("id", greaterThan(0));

    }

    @Test
    void assignCardToLessonTest() {
        var cardId = testCardNotAssignedToLesson1.getId();

        // pre check if card is not in lesson
        assertThat(lesson1.getCards()).noneMatch(c -> c.getId() == cardId);

        given()
            .pathParam("cardSetId", testCardSet.getId())
            .log().ifValidationFails()
            .contentType(ContentType.JSON)
            .post("/" + lesson1.getId() + "/assign/" + cardId)
            .then()
            .statusCode(204);

        em.clear();

        Lesson lesson = lessonService.findByIdWithCards(lesson1.getId());

        assertThat(lesson.getCards()).anyMatch(c -> c.getId() == cardId);
    }


    @Test
    void updateALessonTest() {
        String lessonJson = """
            {
                "id": %d,
                "name": "Lesson updated"
            }
            """;

        lessonJson = String.format(lessonJson, lesson1.getId());

        given()
            .pathParam("cardSetId", testCardSet.getId())
            .log().ifValidationFails()
            .body(lessonJson)
            .contentType(ContentType.JSON)
            .put("/" + lesson1.getId())
            .then()
            .statusCode(200)
            .body("id", is(Long.valueOf(lesson1.getId()).intValue()))
            .body("name", is("Lesson updated"));

    }

    private static UserInfo testUserInfo() {
        return new UserInfo(99999, "ittest@example.org", "it", "test");
    }


}
