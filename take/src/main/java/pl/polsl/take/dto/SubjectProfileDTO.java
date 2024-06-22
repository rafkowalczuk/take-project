package pl.polsl.take.dto;

import java.util.List;

public class SubjectProfileDTO {
    private Long subjectId;
    private String name;
    private List<String> lecturersNames; // Lista nazwisk wykładowców

    // Konstruktory, gettery i settery
    public SubjectProfileDTO() {
    }

    public SubjectProfileDTO(Long subjectId, String name, List<String> lecturersNames) {
        this.subjectId = subjectId;
        this.name = name;
        this.lecturersNames = lecturersNames;
    }

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

    public List<String> getLecturersNames() {
        return lecturersNames;
    }

    public void setLecturersNames(List<String> lecturersNames) {
        this.lecturersNames = lecturersNames;
    }
}
