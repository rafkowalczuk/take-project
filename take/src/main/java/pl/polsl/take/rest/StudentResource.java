package pl.polsl.take.rest;


import jakarta.ejb.EJBException;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.StudentDTO;
import pl.polsl.take.ejb.StudentService;
import pl.polsl.take.entity.Student;
import pl.polsl.take.dto.StudentSurveyDTO;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;

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
    public Response addStudent(StudentDTO studentDTO) {
        try {
            studentService.addStudent(studentDTO);
            return Response.status(Response.Status.CREATED).entity(studentDTO).build();
        } catch (EmailAlreadyInUseException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (EJBException e) {
            if (e.getCause() instanceof EmailAlreadyInUseException) {
                return Response.status(Response.Status.CONFLICT).entity(e.getCause().getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

    @PUT
    @Path("/{studentId}")
    public Response updateStudent(@PathParam("studentId") Long studentId, StudentDTO studentDTO) {
        try {
            studentService.updateStudent(studentId, studentDTO);
            return Response.ok(studentDTO).build();
        } catch (EmailAlreadyInUseException e) {
            return Response.status(Response.Status.CONFLICT).entity(e.getMessage()).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (EJBException e) {
            if (e.getCause() instanceof EmailAlreadyInUseException) {
                return Response.status(Response.Status.CONFLICT).entity(e.getCause().getMessage()).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Internal Server Error").build();
        }
    }

   @GET
    @Path("/surveys/{studentId}")
    public Response getStudentSurveys(@PathParam("studentId") Long studentId) {
        List<StudentSurveyDTO> surveys = studentService.getStudentSurveys(studentId);
        return Response.ok(surveys).build();
    }
    @GET
    @Path("/email/{email}")
    public Response getStudentByEmail(@PathParam("email") String email) {
        try {
            Student student = studentService.getStudentByEmail(email);
            return Response.ok(student).build();
        } catch (EmailNotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (EJBException ejbEx) {
            Throwable cause = ejbEx.getCause();
            if (cause instanceof EmailNotFoundException) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(cause.getMessage())
                        .build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal Server Error")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Internal Server Error")
                    .build();
        }
    }
    @GET
    @Path("/profile/{studentId}")
    public Response getStudentProfile(@PathParam("studentId") Long studentId) {
        StudentDTO studentProfile = studentService.getStudentProfile(studentId);
        if (studentProfile == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Student with ID " + studentId + " not found")
                    .build();
        }
        return Response.ok(studentProfile).build();
    }


    @DELETE
    @Path("/email/{email}")
    public Response deleteStudentByEmail(@PathParam("email") String email) {
        try {
            studentService.deleteStudentByEmail(email);
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
