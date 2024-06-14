package pl.polsl.take.entity;


import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "lecturer")
public class Lecturer {

    public Lecturer(Long lecturerId, String firstName, String lastName, String email, Set<Survey> surveys, Set<Subject> subjects) {
        this.lecturerId = lecturerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.surveys = surveys;
        this.subjects = subjects;
    }

    public Lecturer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecturer_id")
    private Long lecturerId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    private String email;

 /*   @ManyToMany
    @JoinTable(
            name = "lecturer_subject",
            joinColumns = @JoinColumn(name = "lecturer_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    */
 @OneToMany(mappedBy = "lecturer")
 @JsonbTransient
    private Set<Subject> subjects;

    @JsonbTransient
    @OneToMany(mappedBy = "lecturer")
    private Set<Survey> surveys;

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }

    public Set<Survey> getSurveys() {
        return surveys;
    }

    public void setSurveys(Set<Survey> surveys) {
        this.surveys = surveys;
    }
}
