package de.xenadu.learningcards.controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class IndexController {


    @GET
    @Path("/")
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Learning Cards";
    }

}
