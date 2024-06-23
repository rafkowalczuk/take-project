package pl.polsl.take.ejb;


import jakarta.ejb.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.LecturerDTO;
import pl.polsl.take.dto.LecturerProfileDTO;
import pl.polsl.take.entity.*;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;
import pl.polsl.take.validator.EmailValidator;

import java.util.*;
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
                throw new EmailAlreadyInUseException("Email " + lecturerDTO.getEmail() + " is already in use.");
            }

            Lecturer lecturer = new Lecturer();
            lecturer.setFirstName(lecturerDTO.getFirstName());
            lecturer.setLastName(lecturerDTO.getLastName());
            lecturer.setEmail(lecturerDTO.getEmail());
            em.persist(lecturer);

            if (lecturerDTO.getSubjectIds() != null && !lecturerDTO.getSubjectIds().isEmpty()) {
                List<Subject> subjects = em.createQuery("SELECT s FROM Subject s WHERE s.subjectId IN :ids", Subject.class)
                        .setParameter("ids", lecturerDTO.getSubjectIds())
                        .getResultList();
                for (Subject subject : subjects) {
                    lecturer.getSubjects().add(subject);
                    subject.getLecturers().add(lecturer);
                    em.merge(subject);
                    createSurveyForLecturer(lecturer, subject);
                }
            }
        } catch (EmailAlreadyInUseException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    public void updateLecturer(Long lecturerId, LecturerDTO lecturerDTO) {
        Lecturer lecturer = em.find(Lecturer.class, lecturerId);
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer not found.");
        }

        if (lecturerDTO.getEmail() != null) {
            EmailValidator.validate(lecturerDTO.getEmail());

            Long count = em.createQuery("SELECT COUNT(l) FROM Lecturer l WHERE l.email = :email AND l.id != :id", Long.class)
                    .setParameter("email", lecturerDTO.getEmail())
                    .setParameter("id", lecturerId)
                    .getSingleResult();
            if (count > 0) {
                throw new EmailAlreadyInUseException("Email " + lecturerDTO.getEmail() + " is already in use.");
            }

            lecturer.setEmail(lecturerDTO.getEmail());
        }

        if (lecturerDTO.getFirstName() != null) {
            lecturer.setFirstName(lecturerDTO.getFirstName());
        }
        if (lecturerDTO.getLastName() != null) {
            lecturer.setLastName(lecturerDTO.getLastName());
        }

        em.merge(lecturer);

        if (lecturerDTO.getSubjectIds() != null) {
            Set<Long> newSubjectIds = new HashSet<>(lecturerDTO.getSubjectIds());
            Set<Subject> currentSubjects = lecturer.getSubjects();

            for (Subject currentSubject : new HashSet<>(currentSubjects)) {
                if (!newSubjectIds.contains(currentSubject.getSubjectId())) {
                    currentSubjects.remove(currentSubject);
                    currentSubject.getLecturers().remove(lecturer);
                    em.merge(currentSubject);
                }
            }

            Set<Subject> newSubjects = new HashSet<>(em.createQuery("SELECT s FROM Subject s WHERE s.subjectId IN :ids", Subject.class)
                    .setParameter("ids", newSubjectIds)
                    .getResultList());

            for (Subject newSubject : newSubjects) {
                if (!currentSubjects.contains(newSubject)) {
                    currentSubjects.add(newSubject);
                    newSubject.getLecturers().add(lecturer);
                    em.merge(newSubject);
                    createSurveyForLecturer(lecturer, newSubject);
                }
            }

            lecturer.setSubjects(currentSubjects);
            em.merge(lecturer);
        }
    }



    private void createSurveyForLecturer(Lecturer lecturer, Subject subject) {
        Survey survey = new Survey();
        survey.setLecturer(lecturer);
        survey.setSubject(subject);
        survey.setName(lecturer.getFirstName() + " " + lecturer.getLastName() + " - Survey for " + subject.getName());
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
            return null;
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
    public void deleteLecturerByEmail(String email) {
        try {
            Lecturer lecturer = em.createQuery("SELECT l FROM Lecturer l WHERE l.email = :email", Lecturer.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (lecturer != null) {

                List<Answer> answers = em.createQuery("SELECT a FROM Answer a WHERE a.survey.lecturer = :lecturer", Answer.class)
                        .setParameter("lecturer", lecturer)
                        .getResultList();
                for (Answer answer : answers) {
                    em.remove(answer);
                }


                List<Survey> surveys = em.createQuery("SELECT s FROM Survey s WHERE s.lecturer = :lecturer", Survey.class)
                        .setParameter("lecturer", lecturer)
                        .getResultList();
                for (Survey survey : surveys) {
                    em.remove(survey);
                }


                for (Subject subject : lecturer.getSubjects()) {
                    subject.getLecturers().remove(lecturer);
                    em.merge(subject);
                }


                em.remove(lecturer);
            }
        } catch (NoResultException e) {
            throw new EmailNotFoundException("No lecturer found with the email: " + email);
        }
    }
}


