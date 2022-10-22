package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.service.extern.api.UserService;
import io.quarkus.oidc.IdToken;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GetUserInfo {

    @Inject
    @IdToken
    JsonWebToken idToken;

    @Inject
    UserService userService;

    // ToDo: must be configurable over some testuser=test@test.de and userTestUser=true properties
    public UserInfo authenticatedUser() {
        final String email = idToken.getClaim("email");

        if (email.equalsIgnoreCase("test@test.de")) {
            return new UserInfo(12, "test@test.de", "Testuser", "Test");
        }

        if (email.equalsIgnoreCase("test@example.org")) {
            return new UserInfo(1, "test@example.org", "Stefan", "Simon");
        }

        throw new IllegalStateException("No User found");
//        return userService.getUserByEmail(email);
    }

}
