package pl.polsl.take.dto;

import java.util.Date;

public class StudentSurveyDTO {
    private Long surveyId;
    private String surveyName;

    public StudentSurveyDTO(Long surveyId, String surveyName) {
        this.surveyId = surveyId;
        this.surveyName = surveyName;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getSurveyName() {
        return surveyName;
    }

    public void setSurveyName(String surveyName) {
        this.surveyName = surveyName;
    }
}