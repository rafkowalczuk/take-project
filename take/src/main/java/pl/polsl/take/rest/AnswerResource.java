package pl.polsl.take.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.AnswerDTO;
import pl.polsl.take.ejb.AnswerService;

@Path("/answers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AnswerResource {

    @Inject
    private AnswerService answerService;

    @POST
    public Response submitAnswers(AnswerDTO answersDTO) {
        answerService.saveAnswers(answersDTO);
        return Response.status(Response.Status.CREATED).build();
    }
}
