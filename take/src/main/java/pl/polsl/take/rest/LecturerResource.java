package pl.polsl.take.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.LecturerDTO;
import pl.polsl.take.dto.LecturerProfileDTO;
import pl.polsl.take.ejb.LecturerService;
import pl.polsl.take.entity.Lecturer;

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
        lecturerService.addLecturer(lecturerDTO);
        return Response.status(Response.Status.CREATED).entity(lecturerDTO).build();
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

}