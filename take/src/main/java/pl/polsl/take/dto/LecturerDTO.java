package pl.polsl.take.dto;


import java.util.List;

public class LecturerDTO {
    private Long lecturerId;
    private String firstName;
    private String lastName;
    private String email;
    private List<Long> subjectIds;

    public LecturerDTO() {}

    public LecturerDTO(Long lecturerId, String firstName, String lastName, String email, List<Long> subjectIds) {
        this.lecturerId = lecturerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.subjectIds = subjectIds;
    }

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

    public List<Long> getSubjectIds() {
        return subjectIds;
    }

    public void setSubjectIds(List<Long> subjectIds) {
        this.subjectIds = subjectIds;
    }


}

