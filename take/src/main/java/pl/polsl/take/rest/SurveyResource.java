package pl.polsl.take.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pl.polsl.take.dto.SurveyDTO;
import pl.polsl.take.dto.SurveyResultDTO;
import pl.polsl.take.ejb.SurveyService;
import pl.polsl.take.entity.Survey;

import java.util.List;

@Path("/surveys")
@Produces(MediaType.APPLICATION_JSON)
public class SurveyResource {

    @Inject
    private SurveyService surveyService;

    @GET
    @Path("/results/{surveyId}")
    public Response getSurveyResults(@PathParam("surveyId") Long surveyId) {
        List<SurveyResultDTO> results = surveyService.getSurveyResults(surveyId);
        return Response.ok(results).build();
    }

    @GET
    public Response getAllSurveys() {
        List<Survey> surveys = surveyService.getAllSurveys();
        return Response.ok(surveys).build();
    }

    @GET
    @Path("/{surveyId}")
    public Response getSurveyWithQuestions(@PathParam("surveyId") Long surveyId) {
        SurveyDTO survey = surveyService.getSurveyWithQuestions(surveyId);
        if (survey == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Survey not found").build();
        }
        return Response.ok(survey).build();
    }

}
