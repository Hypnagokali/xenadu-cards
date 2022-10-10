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

    public UserInfo authenticatedUser() {
        final String email = idToken.getClaim("email");
        return userService.getUserByEmail(email);
    }

}
