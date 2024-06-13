package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.StudentSurveyDTO;
import pl.polsl.take.entity.Student;

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

    public void addStudent(Student student) {
        em.persist(student);
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
