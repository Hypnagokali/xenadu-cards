package de.xenadu.learningcards.service;

import io.quarkus.oidc.IdToken;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GetUserInfo {

        @Inject
        @IdToken
        JsonWebToken idToken;

}
