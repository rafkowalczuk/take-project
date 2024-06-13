package pl.polsl.take.dto;


public class QuestionDTO {
    private Long questionId;
    private String content;

    public QuestionDTO(Long questionId, String content) {
        this.questionId = questionId;
        this.content = content;
    }

    // Getters and setters
    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

