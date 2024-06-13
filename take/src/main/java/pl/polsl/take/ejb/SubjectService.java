package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.SubjectDTO;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Subject;
import java.util.List;

@Stateless
public class SubjectService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public List<Subject> getAllSubjects() {
        return em.createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
    }


    public void addSubject(SubjectDTO subjectDTO) {
        try {
            Lecturer lecturer = em.find(Lecturer.class, subjectDTO.getLecturerId());
            if (lecturer == null) {
                throw new IllegalArgumentException("Lecturer with ID " + subjectDTO.getLecturerId() + " does not exist");
            }
            Subject subject = new Subject();
            subject.setName(subjectDTO.getName());
            subject.setLecturer(lecturer);
            em.persist(subject);

            lecturer.getSubjects().add(subject);
            em.merge(lecturer);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());

            throw e;
        }
    }


}
