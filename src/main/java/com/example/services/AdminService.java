package com.example.services;

import com.example.database.JsonDatabaseManager;
import com.example.models.Admin;
import com.example.models.Course;
import com.example.models.CourseStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminService {

    private static AdminService instance;
    private JsonDatabaseManager db;

    private AdminService() {
        db = JsonDatabaseManager.getInstance();
    }

    public static AdminService getInstance() {
        if (instance == null) instance = new AdminService();
        return instance;
    }

    //Courses Pending Review
    public List<Course> getPendingCourses() {
        List<Course> pending = new ArrayList<>();
        for (Course c : db.getCourses()) {
            if (c.getStatus() == CourseStatus.PENDING)
                pending.add(c);
        }
        return pending;
    }

    //Approve Course
    public boolean approveCourse(int courseId, Admin admin) {
        Course c = getCourseById(courseId);
        if (c == null || admin == null) return false;

        c.setStatus(CourseStatus.APPROVED);
        c.setApprovedByAdminId(admin.getUserId());
        c.setRejectionReason(null);

        db.saveCourses();
        return true;
    }

    //Reject Course
    public boolean rejectCourse(int courseId, Admin admin, String reason) {
        Course c = getCourseById(courseId);
        if (c == null || admin == null) return false;

        c.setStatus(CourseStatus.REJECTED);
        c.setApprovedByAdminId(admin.getUserId());
        c.setRejectionReason(reason);

        db.saveCourses();
        return true;
    }

    //View All Approved Courses
    public List<Course> getApprovedCourses() {
        List<Course> approved = new ArrayList<>();
        for (Course c : db.getCourses()) {
            if (c.getStatus() == CourseStatus.APPROVED)
                approved.add(c);
        }
        return approved;
    }

    //Internal Getter
    private Course getCourseById(int id) {
        for (Course c : db.getCourses()) {
            if (c.getCourseId() == id)
                return c;
        }
        return null;
    }
}
