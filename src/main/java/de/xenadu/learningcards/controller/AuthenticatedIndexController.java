package de.xenadu.learningcards.controller;

import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.OidcSession;
import io.quarkus.oidc.RefreshToken;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.groups.UniAwait;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jboss.resteasy.reactive.NoCache;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/api")
@Authenticated
@NoCache
public class AuthenticatedIndexController {

    /**
     * Injection point for the ID Token issued by the OpenID Connect Provider
     */
    @Inject
    @IdToken
    JsonWebToken idToken;


    @Inject
    JsonWebToken accessToken;

    @Inject
    OidcSession oidcSession;

    @GET
    @Path("/dev")
    @Produces(MediaType.TEXT_PLAIN)
    public void startDev(@Context HttpServletResponse response) {
        try {
            response.sendRedirect("http://localhost:8081");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GET
    @Path("/logout")
    @Produces(MediaType.TEXT_PLAIN)
    public String logout(@Context HttpServletResponse response) {
        final UniAwait<Void> await = oidcSession.logout().await();
        await.indefinitely();

        final String encode = URLEncoder.encode("http://localhost:7070/", StandardCharsets.ISO_8859_1);

        final String issuer = accessToken.getIssuer();
        try {
            response.sendRedirect(
                    issuer + "/protocol/openid-connect/logout" +
                            "?post_logout_redirect_uri=" + encode + "&" +
                            "id_token_hint=" + idToken.getRawToken());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "you are logged out";
    }

}
