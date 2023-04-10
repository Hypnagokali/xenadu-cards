package de.xenadu.learningcards.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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
import javax.inject.Inject;
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
        testCardSet.setUser(testUserInfo());
        Card card1 = new Card("card1", "Karte 1");
        Card card2 = new Card("card2", "Karte 2");
        Card card3 = new Card("card3", "Karte 3");
        testCardSet.addCard(card1);
        testCardSet.addCard(card2);
        testCardSet.addCard(card3);
        cardSetRepository.persist(testCardSet);

        Lesson lesson1 = new Lesson("Lesson 1");
        lesson1.setCardSet(testCardSet);
        lesson1.addCard(card1);

        Lesson lesson2 = new Lesson("Lesson 2");
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

    private static UserInfo testUserInfo() {
        return new UserInfo(99999, "ittest@example.org", "it", "test");
    }


}
