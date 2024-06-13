package pl.polsl.take.dto;

import jakarta.json.bind.annotation.JsonbDateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SurveyDTO {
    private Long surveyId;
    private String name;


    private String dateCreated;
    private List<QuestionDTO> questions;

    public SurveyDTO(Long surveyId, String name, Date dateCreated, List<QuestionDTO> questions) {
        this.surveyId = surveyId;
        this.name = name;
        this.dateCreated = new SimpleDateFormat("yyyy-MM-dd").format(dateCreated);
        this.questions = questions;
    }

    // Getters and setters
    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }
}

