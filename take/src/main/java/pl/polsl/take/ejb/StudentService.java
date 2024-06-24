package pl.polsl.take.ejb;

import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.StudentDTO;
import pl.polsl.take.dto.StudentSurveyDTO;
import pl.polsl.take.entity.Answer;
import pl.polsl.take.entity.Student;
import pl.polsl.take.exceptions.EmailAlreadyInUseException;
import pl.polsl.take.exceptions.EmailNotFoundException;
import pl.polsl.take.validator.EmailValidator;

import java.util.List;

@Stateless
public class StudentService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;


    public List<Student> getAllStudents() {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public List<StudentSurveyDTO> getStudentSurveys(Long studentId) {
        return em.createQuery(
                        "SELECT new pl.polsl.take.dto.StudentSurveyDTO(a.survey.id, a.survey.name) " +
                                "FROM Answer a WHERE a.student.id = :studentId GROUP BY a.survey.id, a.survey.name",
                        StudentSurveyDTO.class)
                .setParameter("studentId", studentId)
                .getResultList();
    }

    public void addStudent(StudentDTO studentDTO) {
        try {
            EmailValidator.validate(studentDTO.getEmail());

            Long count = em.createQuery("SELECT COUNT(s) FROM Student s WHERE s.email = :email", Long.class)
                    .setParameter("email", studentDTO.getEmail())
                    .getSingleResult();
            if (count > 0) {
                throw new EmailAlreadyInUseException("Email " + studentDTO.getEmail() + " is already in use.");
            }

            Student student = new Student();
            student.setFirstName(studentDTO.getFirstName());
            student.setLastName(studentDTO.getLastName());
            student.setEmail(studentDTO.getEmail());

            em.persist(student);
        } catch (EmailAlreadyInUseException | IllegalArgumentException e) {
            throw e; // Rzucenie wyjątku bez opakowania
        } catch (Exception e) {
            throw new EJBException(e); // Opakowanie innych nieoczekiwanych wyjątków
        }
    }

    public void updateStudent(Long studentId, StudentDTO studentDTO) {
        try {
            Student student = em.find(Student.class, studentId);
            if (student == null) {
                throw new IllegalArgumentException("Student not found.");
            }

            if (studentDTO.getEmail() != null) {
                EmailValidator.validate(studentDTO.getEmail());

                Long count = em.createQuery("SELECT COUNT(s) FROM Student s WHERE s.email = :email AND s.id != :id", Long.class)
                        .setParameter("email", studentDTO.getEmail())
                        .setParameter("id", studentId)
                        .getSingleResult();
                if (count > 0) {
                    throw new EmailAlreadyInUseException("Email " + studentDTO.getEmail() + " is already in use.");
                }

                student.setEmail(studentDTO.getEmail());
            }
            if (studentDTO.getFirstName() != null) {
                student.setFirstName(studentDTO.getFirstName());
            }
            if (studentDTO.getLastName() != null) {
                student.setLastName(studentDTO.getLastName());
            }

            em.merge(student);
        } catch (EmailAlreadyInUseException | IllegalArgumentException e) {
            throw e; // Rzucenie wyjątku bez opakowania
        } catch (Exception e) {
            throw new EJBException(e); // Opakowanie innych nieoczekiwanych wyjątków
        }
    }


    public Student getStudentByEmail(String email) {
        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EmailNotFoundException("Student with email " + email + " not found");
        } catch (Exception e) {
            throw new EJBException(e); // Opakowanie innych nieoczekiwanych wyjątków
        }
    }
    public void deleteStudentByEmail(String email) {
        try {
            Student student = em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getSingleResult();

            if (student != null) {
                List<Answer> answers = em.createQuery("SELECT a FROM Answer a WHERE a.student = :student", Answer.class)
                        .setParameter("student", student)
                        .getResultList();
                for (Answer answer : answers) {
                    em.remove(answer);
                }

                em.remove(student);
            }
        } catch (NoResultException e) {
            throw new EmailNotFoundException("No student found with the email: " + email);
        } catch (Exception e) {
            throw new EJBException(e); // Opakowanie innych nieoczekiwanych wyjątków
        }
    }


    public StudentDTO getStudentProfile(Long studentId) {

        Student student = em.find(Student.class, studentId);
        if (student == null) {
            return null;
        }


        List<StudentSurveyDTO> surveys = em.createQuery(
                        "SELECT new pl.polsl.take.dto.StudentSurveyDTO(a.survey.id, a.survey.name) " +
                                "FROM Answer a WHERE a.student.id = :studentId GROUP BY a.survey.id, a.survey.name",
                        StudentSurveyDTO.class)
                .setParameter("studentId", studentId)
                .getResultList();

        return new StudentDTO(
                student.getStudentId(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                surveys
        );
    }



}
