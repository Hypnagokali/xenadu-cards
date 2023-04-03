package de.xenadu.learningcards.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import de.xenadu.learningcards.config.DevOidcSession;
import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.entities.CardSet;
import de.xenadu.learningcards.persistence.repositories.CardSetRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.jwt.auth.cdi.NullJsonWebToken;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CardControllerIntegrationIT {

    @Inject
    CardSetRepository cardSetRepository;
    private long cardSetId;


    @BeforeEach
    public void setUp() throws Exception {
        createTestDataWithDifferentRepStates();
    }

    @Test
    public void getCardsByRepetitionState() throws Exception {
        given()
                .log()
                .ifValidationFails()
                .pathParam("repState", 1)
                .pathParam("cardSetId", cardSetId)
                .when()
                .get("/api/card-sets/{cardSetId}/cards/rep-state/{repState}")
                .then()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].front", is("good"))
                .body("[1].back", is("bad 2"));
    }

    @Transactional
    public void createTestDataWithDifferentRepStates() {
        CardSet cardSet = new CardSet(0, "Test CardSet");

        cardSet.addCard(new Card("front", "back", 0));
        cardSet.addCard(new Card("good", "bad", 1));
        cardSet.addCard(new Card("good 2", "bad 2", 1));

        cardSetRepository.persist(cardSet);
        this.cardSetId = cardSet.getId();
    }

    @Default
    @ApplicationScoped
    public static class TestJsonWebToken extends NullJsonWebToken {

    }

    @Default
    @ApplicationScoped
    public static class TestOidcSession extends DevOidcSession {
    }


}