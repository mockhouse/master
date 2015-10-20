package com.quick.result;

/**
 * Created by eropate on 26/6/15.
 */
public class QuizResult {
    private String userName;
    private Long questionId;
    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;
    private Long timeToAnswer;
    private Long startTime;


    public QuizResult(String userName,Long questionId,String questionText,String selectedAnswer,String correctAnswer,Long timeToAnswer,Long startTime) {
        this.userName = userName;
        this.questionId = questionId;
        this.questionText = questionText;
        this.selectedAnswer = selectedAnswer;
        this.correctAnswer = correctAnswer;
        this.timeToAnswer = timeToAnswer;
        this.startTime = startTime;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getSelectedAnswer() {
        return selectedAnswer;
    }

    public void setSelectedAnswer(String selectedAnswer) {
        this.selectedAnswer = selectedAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Long getTimeToAnswer() {
        return timeToAnswer;
    }

    public void setTimeToAnswer(Long timeToAnswer) {
        this.timeToAnswer = timeToAnswer;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }


    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append(questionText + "\n");
        info.append(selectedAnswer);
        if(selectedAnswer==correctAnswer || selectedAnswer.equals(correctAnswer)) {
            info.append("(correct)");
        } else {
            info.append("(incorrect)");
        }
        info.append("(Time to answer : " + timeToAnswer + ")");
        return info.toString();
    }
}
