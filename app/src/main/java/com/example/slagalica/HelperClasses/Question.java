package com.example.slagalica.HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private String question;
    private List<String> possibleAnswers;
    private int correctAnswer;

    public Question(String question)
    {
        this.question = question;
        possibleAnswers = new ArrayList<>();
    }

    public String getQuestion() {
        return question;
    }


    public List<String> getPossibleAnswers() {
        return possibleAnswers;
    }

    public void addPossibleAnswers(String possibleAnswers) {
        this.possibleAnswers.add(possibleAnswers);
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
