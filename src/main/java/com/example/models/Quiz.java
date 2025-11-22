package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}
    public Quiz(List<Question> questions){ this.questions = questions != null ? questions : new ArrayList<>(); }

    public List<Question> getQuestions(){ return questions; }
    public void setQuestions(List<Question> questions){ this.questions = questions != null ? questions : new ArrayList<>(); }
}
