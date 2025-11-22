package com.example.models;

import java.time.LocalDate;

public class Enrollment {
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private boolean completed;

    public Enrollment() {}

    public Enrollment(int studentId, int courseId){
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = LocalDate.now();
        this.completed = false;
    }

    public int getStudentId(){ return studentId; }
    public void setStudentId(int s){ this.studentId = s; }
    public int getCourseId(){ return courseId; }
    public void setCourseId(int c){ this.courseId = c; }
    public LocalDate getEnrollmentDate(){ return enrollmentDate; }
    public void setEnrollmentDate(LocalDate d){ this.enrollmentDate = d; }
    public boolean isCompleted(){ return completed; }
    public void setCompleted(boolean b){ this.completed = b; }
}
