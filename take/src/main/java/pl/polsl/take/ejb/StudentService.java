package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.StudentDTO;
import pl.polsl.take.dto.StudentSurveyDTO;
import pl.polsl.take.entity.Student;
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
                throw new IllegalArgumentException("Email " + studentDTO.getEmail() + " is already in use.");
            }

            Student student = new Student();
            student.setFirstName(studentDTO.getFirstName());
            student.setLastName(studentDTO.getLastName());
            student.setEmail(studentDTO.getEmail());

            em.persist(student);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }

    public void updateStudent(Long studentId, StudentDTO studentDTO) {
        Student student = em.find(Student.class, studentId);
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }

        if (studentDTO.getFirstName() != null) {
            student.setFirstName(studentDTO.getFirstName());
        }
        if (studentDTO.getLastName() != null) {
            student.setLastName(studentDTO.getLastName());
        }
        if (studentDTO.getEmail() != null) {
            EmailValidator.validate(studentDTO.getEmail());
            student.setEmail(studentDTO.getEmail());
        }
        em.merge(student);
    }


    public Student getStudentByEmail(String email) {
        try {
            return em.createQuery("SELECT s FROM Student s WHERE s.email = :email", Student.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
