package com.example.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.database.JsonDatabaseManager;
import com.example.services.CourseService;

public class Student extends User {
    private List<Integer> enrolledCourseIds = new ArrayList<>();
    private List<Integer> completedLessonIds = new ArrayList<>();
    private Map<Integer, Integer> quizScores = new HashMap<>();
    private List<Certificate> certificates = new ArrayList<>();

    public Student() { super("", "", "", "student"); }
    public Student(String username, String email, String passwordHash){
        super(username, email, passwordHash, "student");
    }

    // persisted IDs
    public List<Integer> getEnrolledCourseIds() { if(enrolledCourseIds==null) enrolledCourseIds = new ArrayList<>(); return enrolledCourseIds; }
    public void setEnrolledCourseIds(List<Integer> enrolledCourseIds) { this.enrolledCourseIds = enrolledCourseIds != null ? enrolledCourseIds : new ArrayList<>(); }

    public List<Integer> getCompletedLessonIds() { if(completedLessonIds==null) completedLessonIds = new ArrayList<>(); return completedLessonIds; }
    public void setCompletedLessonIds(List<Integer> completedLessonIds) { this.completedLessonIds = completedLessonIds != null ? completedLessonIds : new ArrayList<>(); }

    public Map<Integer,Integer> getQuizScores(){ if(quizScores==null) quizScores = new HashMap<>(); return quizScores; }
    public void setQuizScores(Map<Integer,Integer> map){ this.quizScores = map != null ? map : new HashMap<>(); }

    public List<Certificate> getCertificates(){ if(certificates==null) certificates = new ArrayList<>(); return certificates; }
    public void setCertificates(List<Certificate> certs){ this.certificates = certs != null ? certs : new ArrayList<>(); }

    // convenience: resolve Course objects (used by UI)
    public List<Course> getEnrolledCourses(){
        List<Course> out = new ArrayList<>();
        for(Integer id : getEnrolledCourseIds()){
            Course c = CourseService.getInstance().getCourseById(id);
            if(c != null) out.add(c);
        }
        return out;
    }

    // enroll by Course object (keeps both sides)
    public void enrollCourse(Course course){
        if(course == null) return;
        if(!getEnrolledCourseIds().contains(course.getCourseId())){
            getEnrolledCourseIds().add(course.getCourseId());
            JsonDatabaseManager.getInstance().saveUsers();
        }
        course.enrollStudentById(this.getUserId());
        JsonDatabaseManager.getInstance().saveCourses();
    }

    // mark lesson completed using lesson object
    public boolean markLessonCompleted(Course course, Lesson lesson){
        if(lesson == null) return false;
        if(!getCompletedLessonIds().contains(lesson.getLessonId())){
            getCompletedLessonIds().add(lesson.getLessonId());
            JsonDatabaseManager.getInstance().saveUsers();
            return true;
        }
        return false;
    }

    public int getProgress(Course course){
        List<Lesson> lessons = course.getLessons();
        if(lessons == null || lessons.isEmpty()) return 0;
        long completed = lessons.stream().filter(l -> getCompletedLessonIds().contains(l.getLessonId())).count();
        return (int)((completed * 100) / lessons.size());
    }

    // quiz score helpers
    public void saveQuizScore(int lessonId, int score){
        getQuizScores().put(lessonId, score);
        JsonDatabaseManager.getInstance().saveUsers();
    }
    public Integer getQuizScore(int lessonId){
        return getQuizScores().getOrDefault(lessonId, null);
    }

    public void addCertificate(Certificate c){
        if(c == null) return;
        getCertificates().add(c);
        JsonDatabaseManager.getInstance().saveUsers();
    }
}
