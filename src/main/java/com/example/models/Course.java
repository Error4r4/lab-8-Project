package com.example.models;

import java.util.ArrayList;
import java.util.List;
import com.example.database.JsonDatabaseManager;

public class Course {
    private static int courseCounter = 1;

    private int courseId;
    private String title;
    private String description;

    // persisted link to instructor
    private String instructorEmail;

    // lessons are objects inside course
    private List<Lesson> lessons = new ArrayList<>();

    // store student IDs in JSON to avoid circular refs
    private List<Integer> enrolledStudentIds = new ArrayList<>();

    // transient cached object (not serialized)
    private transient Instructor instructor;

    private CourseStatus status = CourseStatus.PENDING;
    private Integer approvedByAdminId = null;
    private String rejectionReason = null;

    public Course() { this.courseId = courseCounter++; }

    public Course(String title, String description, Instructor instructor){
        this.courseId = courseCounter++;
        this.title = title != null ? title : "Untitled Course";
        this.description = description != null ? description : "";
        setInstructor(instructor);
    }

    public int getCourseId() { return courseId; }
    public void setCourseId(int id){
        this.courseId = id;
        if(id >= courseCounter) courseCounter = id + 1;
    }

    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title != null ? title : this.title; }

    public String getDescription(){ return description; }
    public void setDescription(String desc){ this.description = desc != null ? desc : this.description; }

    public String getInstructorEmail(){ return instructorEmail; }
    public void setInstructorEmail(String email){ this.instructorEmail = email; }

    // ===== Instructor convenience (used by UI) =====
    public Instructor getInstructor(){
        if(this.instructor == null && instructorEmail != null){
            for(User u : JsonDatabaseManager.getInstance().getUsers()){
                if(u instanceof Instructor && u.getEmail().equals(instructorEmail)){
                    this.instructor = (Instructor) u;
                    break;
                }
            }
        }
        return this.instructor;
    }

    public void setInstructor(Instructor inst){
        this.instructor = inst;
        this.instructorEmail = (inst != null) ? inst.getEmail() : null;
        // Also ensure instructor created-course ids list updated
        if(inst != null){
            inst.addCreatedCourse(this);
            JsonDatabaseManager.getInstance().saveUsers();
        }
        JsonDatabaseManager.getInstance().saveCourses();
    }

    // ==== Lessons ====
    public List<Lesson> getLessons(){ if(lessons==null) lessons = new ArrayList<>(); return lessons; }
    public void setLessons(List<Lesson> lessons){ this.lessons = lessons != null ? lessons : new ArrayList<>(); }
    public void addLesson(Lesson l){ if(l!=null) { getLessons().add(l); JsonDatabaseManager.getInstance().saveCourses(); } }
    public void removeLesson(Lesson l){ if(l!=null && lessons!=null){ lessons.remove(l); JsonDatabaseManager.getInstance().saveCourses(); } }

    // ==== Enrolled students: stored as IDs, but UI expects Student objects ====
    public List<Integer> getEnrolledStudentIds(){ if(enrolledStudentIds==null) enrolledStudentIds = new ArrayList<>(); return enrolledStudentIds; }
    public void setEnrolledStudentIds(List<Integer> ids){ this.enrolledStudentIds = ids != null ? ids : new ArrayList<>(); }

    // Return resolved Student objects (used by Instructor UI)
    public List<Student> getEnrolledStudents(){
        List<Student> out = new ArrayList<>();
        if(enrolledStudentIds == null) return out;
        for(Integer sid : enrolledStudentIds){
            for(User u : JsonDatabaseManager.getInstance().getUsers()){
                if(u instanceof Student && u.getUserId() == sid){
                    out.add((Student) u);
                    break;
                }
            }
        }
        return out;
    }

    // Enroll helpers keep IDs and persist
    public void enrollStudentById(int studentId){
        if(enrolledStudentIds == null) enrolledStudentIds = new ArrayList<>();
        if(!enrolledStudentIds.contains(studentId)){
            enrolledStudentIds.add(studentId);
            JsonDatabaseManager.getInstance().saveCourses();
        }
    }

    // convenience: enroll using Student object (keeps both sides consistent)
    public void enrollStudent(Student s){
        if(s == null) return;
        enrollStudentById(s.getUserId());
        // ensure student side knows too
        if(!s.getEnrolledCourseIds().contains(this.courseId)){
            s.getEnrolledCourseIds().add(this.courseId);
            JsonDatabaseManager.getInstance().saveUsers();
        }
        JsonDatabaseManager.getInstance().saveCourses();
    }

    // status & audit
    public CourseStatus getStatus() { return status; }
    public void setStatus(CourseStatus status) { this.status = status; }

    public Integer getApprovedByAdminId() { return approvedByAdminId; }
    public void setApprovedByAdminId(Integer id) { this.approvedByAdminId = id; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String reason) { this.rejectionReason = reason; }

    public static void setCourseCounter(int value){ courseCounter = value; }
}
