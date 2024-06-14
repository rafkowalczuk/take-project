package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.SimpleSubjectDTO;
import pl.polsl.take.dto.SubjectDTO;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Subject;
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



    public void addSubject(SubjectDTO subjectDTO) {
        try {
            Long count = em.createQuery("SELECT COUNT(s) FROM Subject s WHERE s.name = :name", Long.class)
                    .setParameter("name", subjectDTO.getName())
                    .getSingleResult();
            if (count > 0) {
                throw new IllegalArgumentException("Subject with name " + subjectDTO.getName() + " already exists.");
            }

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
