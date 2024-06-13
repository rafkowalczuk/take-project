package pl.polsl.take.ejb;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import pl.polsl.take.dto.SubjectDTO;
import pl.polsl.take.entity.Lecturer;
import pl.polsl.take.entity.Question;
import pl.polsl.take.entity.Subject;
import pl.polsl.take.entity.Survey;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class SubjectService {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    public List<Subject> getAllSubjects() {
        return em.createQuery("SELECT s FROM Subject s", Subject.class).getResultList();
    }


    public void addSubject(SubjectDTO subjectDTO) {
        Lecturer lecturer = em.find(Lecturer.class, subjectDTO.getLecturerId());
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer with ID " + subjectDTO.getLecturerId() + " not found");
        }
        Subject subject = new Subject();
        subject.setName(subjectDTO.getName());
        subject.setLecturer(lecturer);
        em.persist(subject);


        lecturer.getSubjects().add(subject);
        em.merge(lecturer);
    }
    

}
