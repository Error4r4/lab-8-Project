package com.example.models;

import java.util.ArrayList;
import java.util.List;
import com.example.database.JsonDatabaseManager;

public class Instructor extends User {
    // IDs persisted to JSON
    private List<Integer> createdCourseIds = new ArrayList<>();

    // transient cache (not serialized) for convenience in UI
    private transient List<Course> createdCourses;

    public Instructor() { super("", "", "", "instructor"); }
    public Instructor(String username, String email, String passwordHash){
        super(username, email, passwordHash, "instructor");
    }

    // Persisted IDs
    public List<Integer> getCreatedCourseIds() { return createdCourseIds; }
    public void setCreatedCourseIds(List<Integer> ids) { this.createdCourseIds = ids != null ? ids : new ArrayList<>(); }

    // Convenience: load Course objects lazily from DB (not serialized)
    public List<Course> getCreatedCourses(){
        if(createdCourses == null){
            createdCourses = new ArrayList<>();
            if(createdCourseIds != null){
                for(Integer id : createdCourseIds){
                    Course c = JsonDatabaseManager.getInstance().getCourses().stream()
                            .filter(x -> x.getCourseId() == id).findFirst().orElse(null);
                    if(c != null) createdCourses.add(c);
                }
            }
        }
        return createdCourses;
    }

    // update both caches/ids
    public void addCreatedCourse(Course c){
        if(c == null) return;
        if(createdCourseIds == null) createdCourseIds = new ArrayList<>();
        if(!createdCourseIds.contains(c.getCourseId())) createdCourseIds.add(c.getCourseId());
        if(createdCourses == null) createdCourses = new ArrayList<>();
        if(!createdCourses.contains(c)) createdCourses.add(c);
    }

    public void setCreatedCourses(List<Course> courses){
        if(courses == null){
            this.createdCourses = new ArrayList<>();
            this.createdCourseIds = new ArrayList<>();
            return;
        }
        this.createdCourses = courses;
        this.createdCourseIds = new ArrayList<>();
        for(Course c : courses) if(c != null) this.createdCourseIds.add(c.getCourseId());
    }
}
