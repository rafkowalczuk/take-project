package pl.polsl.take.dto;

import java.util.List;

public class StudentDTO {

    private Long studentId;
    private String firstName;
    private String lastName;
    private String email;
    private List<StudentSurveyDTO> surveys;

    public StudentDTO() {
    }

    public StudentDTO(Long studentId, String firstName, String lastName, String email, List<StudentSurveyDTO> surveys) {
        this.studentId = studentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.surveys = surveys;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public List<StudentSurveyDTO> getSurveys() {
        return surveys;
    }

    public void setSurveys(List<StudentSurveyDTO> surveys) {
        this.surveys = surveys;
    }
}
