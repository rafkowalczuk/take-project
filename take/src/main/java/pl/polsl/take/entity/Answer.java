package pl.polsl.take.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "answer")
public class Answer {

    public Answer(Long answerId, Survey survey, Question question, Student student, Integer rating) {
        this.answerId = answerId;
        this.survey = survey;
        this.question = question;
        this.student = student;
        this.rating = rating;
    }

    public Answer() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)  // Możliwość anonimowych odpowiedzi
    private Student student;

    @Column(name = "rating")
    private Integer rating;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    public Survey getSurvey() {
        return survey;
    }

    public void setSurvey(Survey survey) {
        this.survey = survey;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}