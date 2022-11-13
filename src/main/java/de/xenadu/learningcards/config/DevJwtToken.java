package de.xenadu.learningcards.config;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

public class DevJwtToken implements JsonWebToken {

    private final String testUser = "test@test.de";
//    private final String testUser = "test@example.org";

    @Override
    public String getName() {
        return testUser;
    }

    @Override
    public Set<String> getClaimNames() {
        return Set.of("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String claimName) {
        return (T) testUser;
    }
}
