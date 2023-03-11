package de.xenadu.learningcards.service.extern.api;

import de.xenadu.learningcards.domain.UserInfo;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RegisterRestClient(baseUri = "stork://user-service")
public interface UserService {

    @GET
    @Path("/users/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    UserInfo getUserByEmail(String email);

}
