package pl.polsl.take.exceptions;

import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof EJBException) {
            Throwable cause = exception.getCause();
            if (cause instanceof EmailAlreadyInUseException) {
                return Response.status(Response.Status.CONFLICT).entity(cause.getMessage()).build();
            }
            if (cause instanceof EmailNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(cause.getMessage()).build();
            }
            if (cause instanceof IllegalArgumentException) {
                return Response.status(Response.Status.BAD_REQUEST).entity(cause.getMessage()).build();
            }
        }


        if (exception instanceof EmailAlreadyInUseException) {
            return Response.status(Response.Status.CONFLICT).entity(exception.getMessage()).build();
        }
        if (exception instanceof EmailNotFoundException) {
            return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
        }
        if (exception instanceof IllegalArgumentException) {
            return Response.status(Response.Status.BAD_REQUEST).entity(exception.getMessage()).build();
        }

        exception.printStackTrace();
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
    }
}
