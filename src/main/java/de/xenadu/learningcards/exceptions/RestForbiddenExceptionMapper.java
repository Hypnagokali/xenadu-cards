package de.xenadu.learningcards.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestForbiddenExceptionMapper implements ExceptionMapper<RestForbiddenException> {
    @Override
    public Response toResponse(RestForbiddenException exception) {
        return Response.status(403).entity(new ApiError(exception.getMessage(), 403)).build();
    }
}
