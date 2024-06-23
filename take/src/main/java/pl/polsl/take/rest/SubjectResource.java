package pl.polsl.take.rest;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.SimpleSubjectDTO;
import pl.polsl.take.dto.SubjectProfileDTO;
import pl.polsl.take.ejb.SubjectService;
import pl.polsl.take.exceptions.SubjectAlreadyExistsException;
import pl.polsl.take.exceptions.SubjectNotFoundException;


import java.util.List;


@Path("/subjects")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SubjectResource {

    @Inject
    private SubjectService subjectService;

    @GET
    public Response getAllSubjects() {
        List<SimpleSubjectDTO> subjects = subjectService.getAllSubjects();
        return Response.ok(subjects).build();
    }
    @GET
    @Path("/profile/{subjectId}")
    public Response getSubjectProfile(@PathParam("subjectId") Long subjectId) {
        try {
            SubjectProfileDTO subjectProfile = subjectService.getSubjectProfile(subjectId);
            if (subjectProfile != null) {
                return Response.ok(subjectProfile).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Subject with ID " + subjectId + " not found").build();
            }
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    public Response addSubject(SimpleSubjectDTO subjectDTO) {
        try {
            subjectService.addSubject(subjectDTO);
            return Response.status(Response.Status.CREATED).entity(subjectDTO).build();
        } catch (SubjectAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof SubjectAlreadyExistsException) {
                return Response.status(Response.Status.CONFLICT).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @PUT
    @Path("/{subjectId}")
    public Response updateSubject(@PathParam("subjectId") Long subjectId, SimpleSubjectDTO subjectDTO) {
        try {
            subjectService.updateSubject(subjectId, subjectDTO);
            return Response.ok(subjectDTO).build();
        } catch (SubjectAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof SubjectAlreadyExistsException) {
                return Response.status(Response.Status.CONFLICT).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @DELETE
    @Path("/name/{name}")
    public Response deleteSubjectByName(@PathParam("name") String name) {
        try {
            subjectService.deleteSubjectByName(name);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (SubjectNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof SubjectNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

}
