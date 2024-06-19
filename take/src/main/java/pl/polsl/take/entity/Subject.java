package pl.polsl.take.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "subject")
public class Subject {

    public Subject(Long subjectId, String name) {
        this.subjectId = subjectId;
        this.name = name;
        this.lecturers = new HashSet<>();
    }

    public Subject() {
        this.lecturers = new HashSet<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToMany(mappedBy = "subjects")
    private Set<Lecturer> lecturers;


    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Lecturer> getLecturers() {
        return lecturers;
    }

    public void setLecturers(Set<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    // Getters and Setters
}