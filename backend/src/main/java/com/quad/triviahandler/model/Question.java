package com.quad.triviahandler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Question {
    private String question;
    private List<String> answers;

    @JsonIgnore
    private String correctAnswer;

    @JsonProperty("question")
    public String getQuestion() {
        return question;
    }

    @JsonProperty("answers")
    public List<String> getAnswers() {
        return answers;
    }

    @JsonProperty("correct_answer")
    @JsonIgnore
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    @JsonProperty("correct_answer")
    private void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    @JsonProperty("incorrect_answers")
    private void setIncorrectAnswers(List<String> incorrectAnswers) {
        this.answers = new ArrayList<>(incorrectAnswers);
        if (correctAnswer != null) {
            this.answers.add(correctAnswer);
        }
        Collections.shuffle(this.answers);
    }
}
