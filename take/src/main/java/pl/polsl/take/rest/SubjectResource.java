package pl.polsl.take.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.SimpleSubjectDTO;
import pl.polsl.take.dto.SubjectDTO;
import pl.polsl.take.ejb.SubjectService;


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

    @POST
    public Response addSubject(SubjectDTO subjectDTO) {
        subjectService.addSubject(subjectDTO);
        return Response.status(Response.Status.CREATED).build();
    }
}
