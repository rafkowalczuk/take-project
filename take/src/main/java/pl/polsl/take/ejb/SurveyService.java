package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.QuestionDTO;
import pl.polsl.take.dto.SurveyDTO;
import pl.polsl.take.dto.SurveyResultDTO;
import pl.polsl.take.entity.Survey;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SurveyService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public List<SurveyResultDTO> getSurveyResults(Long surveyId) {
        List<SurveyResultDTO> results = em.createQuery(
                        "SELECT new pl.polsl.take.dto.SurveyResultDTO(q.content, AVG(a.rating)) " +
                                "FROM Answer a JOIN a.question q WHERE a.survey.id = :surveyId " +
                                "GROUP BY q.content", SurveyResultDTO.class)
                .setParameter("surveyId", surveyId)
                .getResultList();
        return results;
    }

    public List<Survey> getAllSurveys() {
        return em.createQuery("SELECT s FROM Survey s", Survey.class).getResultList();
    }

    public SurveyDTO getSurveyWithQuestions(Long surveyId) {
        Survey survey = em.find(Survey.class, surveyId);
        if (survey == null) {
            return null;
        }
        List<QuestionDTO> questions = survey.getQuestions().stream()
                .map(question -> new QuestionDTO(question.getQuestionId(), question.getContent()))
                .collect(Collectors.toList());

        return new SurveyDTO(survey.getSurveyId(), survey.getName(), survey.getDateCreated(), questions);
    }

}
