package de.xenadu.learningcards.config;

import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Set;

public class DevJwtToken implements JsonWebToken {
    @Override
    public String getName() {
        return "test@example.org";
    }

    @Override
    public Set<String> getClaimNames() {
        return Set.of("email");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String claimName) {
        return (T) "test@example.org";
    }
}
