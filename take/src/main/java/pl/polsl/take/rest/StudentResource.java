package pl.polsl.take.rest;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.StudentSurveyDTO;
import pl.polsl.take.ejb.StudentService;
import pl.polsl.take.entity.Student;

import java.util.List;

@Path("/students")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Inject
    private StudentService studentService;

    @GET
    public Response getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return Response.ok(students).build();
    }

    @POST
    public Response addStudent(Student student) {
        studentService.addStudent(student);
        return Response.status(Response.Status.CREATED).entity(student).build();
    }

    @GET
    @Path("/{studentId}/surveys")
    public Response getStudentSurveys(@PathParam("studentId") Long studentId) {
        List<StudentSurveyDTO> surveys = studentService.getStudentSurveys(studentId);
        return Response.ok(surveys).build();
    }
    @GET
    @Path("/email/{email}")
    public Response getStudentByEmail(@PathParam("email") String email) {
        Student student = studentService.getStudentByEmail(email);
        if (student == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student with email " + email + " not found")
                    .build();
        }
        return Response.ok(student).build();
    }


}
