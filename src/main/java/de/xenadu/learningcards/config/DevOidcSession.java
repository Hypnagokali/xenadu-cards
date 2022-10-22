package de.xenadu.learningcards.config;

import io.quarkus.oidc.OidcSession;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.Instant;

public class DevOidcSession implements OidcSession {
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
