package de.xenadu.learningcards.config;

import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.oidc.OidcSession;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
@Slf4j
public class ConfigurationOfBeans {

    @Produces
    @IfBuildProfile("dev")
    public JsonWebToken jsonWebToken() {
        log.warn("dev-profile: Mocking JsonWebToken");
        return new DevJwtToken();
    }

    @Produces
    @IfBuildProfile("dev")
    public OidcSession oidcSession() {
        log.warn("dev-profile: Mocking OidcSession");
        return new DevOidcSession();
    }

}
