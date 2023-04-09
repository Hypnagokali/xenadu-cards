package de.xenadu.learningcards.service;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.service.extern.api.UserService;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * Responsible for retrieving an user object from registered user service.
 */
@ApplicationScoped
public class GetUserInfo {

    @Inject
    JsonWebToken bearerToken;

    @RestClient
    @Inject
    UserService userService;

    /**
     * Get {@link UserInfo} object from registered service.
     *
     * @return UserInfo.
     */
    public UserInfo authenticatedUser() {
        final String email = bearerToken.getClaim("email");

        return userService.getUserByEmail(email);
    }

}
