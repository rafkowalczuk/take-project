package pl.polsl.take.ejb;


import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.LecturerDTO;
import pl.polsl.take.dto.LecturerProfileDTO;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Question;
import pl.polsl.take.entity.Survey;
import pl.polsl.take.entity.Subject;
import pl.polsl.take.validator.EmailValidator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class LecturerService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public List<Lecturer> getAllLecturers() {
        return em.createQuery("SELECT l FROM Lecturer l", Lecturer.class).getResultList();
    }


    public void addLecturer(LecturerDTO lecturerDTO) {
        try {
            EmailValidator.validate(lecturerDTO.getEmail());

            Long count = em.createQuery("SELECT COUNT(l) FROM Lecturer l WHERE l.email = :email", Long.class)
                    .setParameter("email", lecturerDTO.getEmail())
                    .getSingleResult();
            if (count > 0) {
                throw new IllegalArgumentException("Email " + lecturerDTO.getEmail() + " is already in use.");
            }

            Lecturer lecturer = new Lecturer();
            lecturer.setFirstName(lecturerDTO.getFirstName());
            lecturer.setLastName(lecturerDTO.getLastName());
            lecturer.setEmail(lecturerDTO.getEmail());
            em.persist(lecturer);

            if (lecturerDTO.getSubjectIds() != null && !lecturerDTO.getSubjectIds().isEmpty()) {
                List<Subject> subjects = em.createQuery("SELECT s FROM Subject s WHERE s.id IN :ids", Subject.class)
                        .setParameter("ids", lecturerDTO.getSubjectIds())
                        .getResultList();
                for (Subject subject : subjects) {
                    subject.setLecturer(lecturer); // Przypisanie wykładowcy do przedmiotu
                    em.merge(subject); // Aktualizacja przedmiotu z nowym przypisaniem wykładowcy
                }
            }

            createSurveyForLecturer(lecturer);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }


    private void createSurveyForLecturer(Lecturer lecturer) {
        Survey survey = new Survey();
        survey.setLecturer(lecturer);
        survey.setName(lecturer.getFirstName() +" "+ lecturer.getLastName() + " - Survey");
        survey.setDateCreated(new Date());
        em.persist(survey);

        createQuestionsForSurvey(survey);
    }

    private void createQuestionsForSurvey(Survey survey) {
        Arrays.asList(
                "Jak oceniasz jasność i przejrzystość przekazywanych informacji przez wykładowcę?",
                "Jak oceniasz zaangażowanie wykładowcy w prowadzenie zajęć?",
                "Jak oceniasz dostępność wykładowcy poza zajęciami?",
                "Jak oceniasz przygotowanie wykładowcy do zajęć?",
                "Jak oceniasz umiejętność wykładowcy do motywowania studentów?",
                "Jak oceniasz interakcję wykładowcy ze studentami podczas zajęć?",
                "Jak oceniasz sposób tłumaczenia trudnych zagadnień przez wykładowcę?",
                "Jak oceniasz wykorzystywanie przez wykładowcę materiałów dydaktycznych?",
                "Jak oceniasz przestrzeganie terminów przez wykładowcę?",
                "Jak oceniasz ogólną postawę i profesjonalizm wykładowcy?"
        ).forEach(content -> {
            Question question = new Question();
            question.setContent(content);
            question.setSurvey(survey);
            em.persist(question);
        });
    }

    public LecturerProfileDTO getLecturerProfile(Long lecturerId) {
        Lecturer lecturer = em.find(Lecturer.class, lecturerId);
        if (lecturer == null) {
            return null;  // Or handle this case as needed
        }
        List<String> subjects = lecturer.getSubjects().stream().map(Subject::getName).collect(Collectors.toList());
        List<String> surveys = lecturer.getSurveys().stream().map(Survey::getName).collect(Collectors.toList());

        return new LecturerProfileDTO(
                lecturer.getLecturerId(),
                lecturer.getFirstName(),
                lecturer.getLastName(),
                lecturer.getEmail(),
                subjects,
                surveys
        );
    }

}