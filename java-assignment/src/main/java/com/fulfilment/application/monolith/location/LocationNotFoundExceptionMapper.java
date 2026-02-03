package com.fulfilment.application.monolith.location;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;


import java.util.Map;

@Provider // This annotation registers the class with the JAX-RS runtime
public class LocationNotFoundExceptionMapper implements ExceptionMapper<LocationNotFoundException> {

    @Override
    public Response toResponse(LocationNotFoundException exception) {
        // Return a 404 status code with a clean JSON body
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                        "error", "Location Not Found",
                        "message", exception.getMessage()
                ))
                .build();
    }
}