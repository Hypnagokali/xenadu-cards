package de.xenadu.learningcards.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class RestNotFoundExceptionMapper implements ExceptionMapper<RestNotFoundException> {
    @Override
    public Response toResponse(RestNotFoundException exception) {
        return Response.status(404).entity(new ApiError(exception.getMessage(), 404)).build();
    }
}
