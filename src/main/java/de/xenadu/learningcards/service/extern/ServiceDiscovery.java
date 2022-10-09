package de.xenadu.learningcards.service.extern;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@RegisterRestClient(baseUri = "http://localhost:8761/eureka/apps")
public interface ServiceDiscovery {

    @GET
    @Path("{serviceName}")
    @Produces(MediaType.APPLICATION_XML)
//    @Consumes(MediaType.APPLICATION_XML)
    String getUserService(@PathParam("serviceName") String serviceName);

}
