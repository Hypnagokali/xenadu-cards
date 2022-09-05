package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class CardControllerIntegrationTest {

    @Inject
    CardRepository cardRepository;

    @BeforeEach
    public void setUp() throws Exception {
        createTestDataWithDifferentRepStates();
    }

    @Test
    public void getCardsByRepetitionState() throws Exception {
        given()
                .pathParam("repState", 1)
                .when().get("/cards/rep-state/{repState}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].front", is("good"))
                .body("[1].back", is("bad 2"));

    }

    @Transactional
    public void createTestDataWithDifferentRepStates() {
        cardRepository.persist(new Card("front", "back", 0));
        cardRepository.persist(new Card("good", "bad", 1));
        cardRepository.persist(new Card("good 2", "bad 2", 1));
    }
}