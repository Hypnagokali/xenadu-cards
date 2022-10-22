package de.xenadu.learningcards.controller;

import de.xenadu.learningcards.domain.UserInfo;
import de.xenadu.learningcards.service.GetUserInfo;
import lombok.RequiredArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequiredArgsConstructor
@Path("/api/user-info")
public class UserInfoController {

    private final GetUserInfo userInfo;

    @GET
    @Path("")
    @Produces(MediaType.APPLICATION_JSON)
    public UserInfo userInfo() {
        return this.userInfo.authenticatedUser();
    }

}
