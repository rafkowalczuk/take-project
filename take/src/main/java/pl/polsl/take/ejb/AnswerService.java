package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.*;
import pl.polsl.take.dto.AnswerDTO;
import pl.polsl.take.entity.Answer;
import pl.polsl.take.entity.Question;
import pl.polsl.take.entity.Student;
import pl.polsl.take.entity.Survey;

@Stateless
public class AnswerService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public void saveAnswers(AnswerDTO answersDTO) {
        for (AnswerDTO answerDTO : answersDTO.getAnswers()) {
            Answer answer = new Answer();
            Survey survey = em.find(Survey.class, answerDTO.getSurveyId());
            Question question = em.find(Question.class, answerDTO.getQuestionId());
            Student student = null;
            if (answerDTO.getStudentId() != null) {
                student = em.find(Student.class, answerDTO.getStudentId());
            }

            answer.setSurvey(survey);
            answer.setQuestion(question);
            answer.setStudent(student);
            answer.setRating(answerDTO.getRating());

            em.persist(answer);
        }
    }
}
