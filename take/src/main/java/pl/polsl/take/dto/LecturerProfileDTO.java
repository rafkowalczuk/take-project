package pl.polsl.take.dto;

import java.util.List;

public class LecturerProfileDTO {
    private Long lecturerId;
    private String firstName;
    private String lastName;
    private String email;
    private List<String> subjects;
    private List<String> surveys;

    public LecturerProfileDTO(Long lecturerId, String firstName, String lastName, String email, List<String> subjects, List<String> surveys) {
        this.lecturerId = lecturerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subjects = subjects;
        this.surveys = surveys;
    }

    // Getters and setters
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<String> subjects) {
        this.subjects = subjects;
    }

    public List<String> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<String> surveys) {
        this.surveys = surveys;
    }
}

