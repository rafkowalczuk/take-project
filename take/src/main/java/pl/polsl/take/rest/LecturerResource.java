package pl.polsl.take.rest;

import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.LecturerDTO;
import pl.polsl.take.dto.LecturerProfileDTO;
import pl.polsl.take.ejb.LecturerService;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;

import java.util.List;

@Path("/lecturers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LecturerResource {

    @Inject
    private LecturerService lecturerService;

    @GET
    public Response getAllLecturers() {
        List<Lecturer> lecturers = lecturerService.getAllLecturers();
        return Response.ok(lecturers).build();
    }

    @POST
    public Response addLecturer(LecturerDTO lecturerDTO) {
        try {
            lecturerService.addLecturer(lecturerDTO);
            return Response.status(Response.Status.CREATED).entity(lecturerDTO).build();
        } catch (EmailAlreadyInUseException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof EmailAlreadyInUseException) {
                return Response.status(Response.Status.CONFLICT).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @GET
    @Path("/profile/{lecturerId}")
    public Response getLecturerProfile(@PathParam("lecturerId") Long lecturerId) {
        LecturerProfileDTO profile = lecturerService.getLecturerProfile(lecturerId);
        if (profile == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Lecturer not found").build();
        }
        return Response.ok(profile).build();
    }

    @PUT
    @Path("/{lecturerId}")
    public Response updateLecturer(@PathParam("lecturerId") Long lecturerId, LecturerDTO lecturerDTO) {
        try {
            lecturerService.updateLecturer(lecturerId, lecturerDTO);
            return Response.ok(lecturerDTO).build();
        } catch (EmailAlreadyInUseException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof EmailAlreadyInUseException) {
                return Response.status(Response.Status.CONFLICT).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @DELETE
    @Path("/email/{email}")
    public Response deleteLecturerByEmail(@PathParam("email") String email) {
        try {
            lecturerService.deleteLecturerByEmail(email);
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (EmailNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof EmailNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND).entity(cause.getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }


}