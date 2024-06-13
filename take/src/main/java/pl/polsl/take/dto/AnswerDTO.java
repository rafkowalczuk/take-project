package pl.polsl.take.dto;


import java.util.List;

public class AnswerDTO {
    private Long surveyId;
    private Long questionId;
    private Long studentId; // Może być null, jeśli odpowiedź ma być anonimowa
    private Integer rating; // Ocena od 1 do 10
    private List<AnswerDTO> answers;


    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }

    public Long getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(Long surveyId) {
        this.surveyId = surveyId;
    }

}
