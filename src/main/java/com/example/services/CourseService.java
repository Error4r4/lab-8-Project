package com.example.services;

import com.example.database.JsonDatabaseManager;
import com.example.models.*;

import java.util.List;
import java.util.stream.Collectors;

public class CourseService {
    private static CourseService instance;
    private final JsonDatabaseManager db = JsonDatabaseManager.getInstance();

    private CourseService() {}

    public static CourseService getInstance() {
        if (instance == null) instance = new CourseService();
        return instance;
    }

    public Course getCourseById(int id){
        return db.getCourses().stream().filter(c -> c.getCourseId() == id).findFirst().orElse(null);
    }

    public List<Course> getAvailableCourses(Student student){
        return db.getCourses().stream()
                .filter(c -> !student.getEnrolledCourseIds().contains(c.getCourseId()))
                .collect(Collectors.toList());
    }
}
