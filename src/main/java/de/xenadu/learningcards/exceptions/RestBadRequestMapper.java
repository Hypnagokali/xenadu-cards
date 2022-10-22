package de.xenadu.learningcards.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestBadRequestMapper implements ExceptionMapper<RestBadRequestException> {
    @Override
    public Response toResponse(RestBadRequestException exception) {
        return Response
                .status(400)
                .entity(new ApiError(exception.getMessage(), 400))
                .build();
    }
}
