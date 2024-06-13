package pl.polsl.take.dto;

public class SubjectDTO {
    private String name;
    private Long lecturerId;

    // Konstruktory, gettery i settery
    public SubjectDTO() {}

    public SubjectDTO(String name, Long lecturerId) {
        this.name = name;
        this.lecturerId = lecturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLecturerId() {
        return lecturerId;
    }

    public void setLecturerId(Long lecturerId) {
        this.lecturerId = lecturerId;
    }
}
