package pl.polsl.take.dto;

public class SurveyResultDTO {
    private String questionContent;
    private double averageRating;

    public SurveyResultDTO(String questionContent, double averageRating) {
        this.questionContent = questionContent;
        this.averageRating = averageRating;
    }

    // Getters and setters
    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
