package com.example.models;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private static int lessonCounter = 1;

    private int lessonId;
    private String title;
    private String content;
    private List<Resource> resources = new ArrayList<>();
    private Quiz quiz;

    public Lesson() { this.lessonId = lessonCounter++; }
    public Lesson(String title, String content){
        this.lessonId = lessonCounter++;
        this.title = title != null ? title : "Untitled Lesson";
        this.content = content != null ? content : "";
    }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int id){ this.lessonId = id; if(id >= lessonCounter) lessonCounter = id + 1; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title != null ? title : this.title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content != null ? content : this.content; }

    public List<Resource> getResources(){ if(resources==null) resources = new ArrayList<>(); return resources; }
    public void setResources(List<Resource> resources){ this.resources = resources != null ? resources : new ArrayList<>(); }
    public void addResource(Resource r){ if(r!=null) getResources().add(r); }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public static void setLessonCounter(int value){ lessonCounter = value; }
}
