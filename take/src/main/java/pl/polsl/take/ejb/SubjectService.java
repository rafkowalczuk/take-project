package pl.polsl.take.ejb;

import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.SimpleSubjectDTO;
import pl.polsl.take.dto.SubjectProfileDTO;
import pl.polsl.take.entity.Answer;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Subject;
import pl.polsl.take.entity.Survey;
import pl.polsl.take.exceptions.SubjectAlreadyExistsException;
import pl.polsl.take.exceptions.SubjectNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SubjectService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public List<SimpleSubjectDTO> getAllSubjects() {
        List<Subject> subjects = em.createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
        return subjects.stream()
                .map(subject -> new SimpleSubjectDTO(subject.getSubjectId(), subject.getName()))
                .collect(Collectors.toList());
    }



    public void addSubject(SimpleSubjectDTO subjectDTO) {
        try {
            Long count = em.createQuery("SELECT COUNT(s) FROM Subject s WHERE s.name = :name", Long.class)
                    .setParameter("name", subjectDTO.getName())
                    .getSingleResult();
            if (count > 0) {
                throw new SubjectAlreadyExistsException("Subject with name " + subjectDTO.getName() + " already exists.");
            }

            Subject subject = new Subject();
            subject.setName(subjectDTO.getName());
            em.persist(subject);
        } catch (SubjectAlreadyExistsException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new EJBException(e);
        }
    }

    public void updateSubject(Long subjectId, SimpleSubjectDTO subjectDTO) {
        Subject subject = em.find(Subject.class, subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("Subject with ID " + subjectId + " does not exist");
        }

        Long count = em.createQuery("SELECT COUNT(s) FROM Subject s WHERE s.name = :name AND s.id != :id", Long.class)
                .setParameter("name", subjectDTO.getName())
                .setParameter("id", subjectId)
                .getSingleResult();
        if (count > 0) {
            throw new SubjectAlreadyExistsException("Subject with name " + subjectDTO.getName() + " already exists.");
        }

        subject.setName(subjectDTO.getName());
        em.merge(subject);
    }

    public void deleteSubjectByName(String name) {
        try {
            Subject subject = em.createQuery("SELECT s FROM Subject s WHERE s.name = :name", Subject.class)
                    .setParameter("name", name)
                    .getSingleResult();

            if (subject != null) {
                List<Answer> answers = em.createQuery("SELECT a FROM Answer a WHERE a.survey.subject = :subject", Answer.class)
                        .setParameter("subject", subject)
                        .getResultList();
                for (Answer answer : answers) {
                    em.remove(answer);
                }

                List<Survey> surveys = em.createQuery("SELECT s FROM Survey s WHERE s.subject = :subject", Survey.class)
                        .setParameter("subject", subject)
                        .getResultList();
                for (Survey survey : surveys) {
                    em.remove(survey);
                }

                for (Lecturer lecturer : subject.getLecturers()) {
                    lecturer.getSubjects().remove(subject);
                    em.merge(lecturer);
                }

                em.remove(subject);
            }
        } catch (NoResultException e) {
            throw new SubjectNotFoundException("No subject found with the name: " + name);
        }
    }

    public SubjectProfileDTO getSubjectProfile(Long subjectId) {
        Subject subject = em.find(Subject.class, subjectId);
        if (subject == null) {
            throw new IllegalArgumentException("Subject with ID " + subjectId + " does not exist.");
        }

        List<String> lecturersFullNames = subject.getLecturers().stream()
                .map(lecturer -> lecturer.getFirstName() + " " + lecturer.getLastName())
                .collect(Collectors.toList());

        return new SubjectProfileDTO(subject.getSubjectId(), subject.getName(), lecturersFullNames);
    }


}
