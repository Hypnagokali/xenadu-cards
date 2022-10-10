package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.persistence.entities.Card;
import de.xenadu.learningcards.persistence.repositories.CardRepository;
import io.quarkus.oidc.OidcSession;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Set;

@QuarkusTest
class CardControllerIntegrationTest {

    @Inject
    CardRepository cardRepository;


    @ApplicationScoped
    @Default
    public static class MockOidcSession implements OidcSession {

        @Override
        public String getTenantId() {
            return null;
        }

        @Override
        public Instant expiresIn() {
            return null;
        }

        @Override
        public Uni<Void> logout() {
            return null;
        }

        @Override
        public JsonWebToken getIdToken() {
            return null;
        }
    }

    @Default
    @ApplicationScoped
    public static class MockIdToken implements JsonWebToken {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Set<String> getClaimNames() {
            return null;
        }

        @Override
        public <T> T getClaim(String claimName) {
            return null;
        }
    }

    @BeforeEach
    public void setUp() throws Exception {
        createTestDataWithDifferentRepStates();
    }

    @Test
    public void getCardsByRepetitionState() throws Exception {
        // ToDo:
//        given()
//                .pathParam("repState", 1)
//                .when().get("/api/cards/rep-state/{repState}")
//                .then()
//                .statusCode(200)
//                .body("size()", is(2))
//                .body("[0].front", is("good"))
//                .body("[1].back", is("bad 2"));

    }

    @Transactional
    public void createTestDataWithDifferentRepStates() {
        cardRepository.persist(new Card("front", "back", 0));
        cardRepository.persist(new Card("good", "bad", 1));
        cardRepository.persist(new Card("good 2", "bad 2", 1));
    }
}