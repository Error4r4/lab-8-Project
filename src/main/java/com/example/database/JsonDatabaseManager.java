// ================= JsonDatabaseManager =================
package com.example.database;

import com.example.models.*;
import com.example.utils.RuntimeTypeAdapterFactory;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.*;

public class JsonDatabaseManager {

    private static JsonDatabaseManager instance;

    private final String usersFilePath = "src/main/resources/users.json";
    private final String coursesFilePath = "src/main/resources/courses.json";

    private List<User> users;
    private List<Course> courses;

    private final Gson gson;

    private JsonDatabaseManager() {

        RuntimeTypeAdapterFactory<User> userAdapterFactory = RuntimeTypeAdapterFactory
                .of(User.class, "role")
                .registerSubtype(Student.class, "student")
                .registerSubtype(Instructor.class, "instructor")
                .registerSubtype(Admin.class, "admin");

        gson = new GsonBuilder()
                .registerTypeAdapterFactory(userAdapterFactory)
                .setPrettyPrinting()
                .create();

        ensureFilesExist();

        users = loadUsers();
        courses = loadCourses();

        fixRelationshipsAndCounters();
    }

    public static JsonDatabaseManager getInstance() {
        if (instance == null) instance = new JsonDatabaseManager();
        return instance;
    }

    private void ensureFilesExist() {
        try {
            File dir = new File("src/main/resources");
            if (!dir.exists()) dir.mkdirs();

            File usersFile = new File(usersFilePath);
            File coursesFile = new File(coursesFilePath);

            if (!usersFile.exists() || usersFile.length() == 0)
                try (FileWriter fw = new FileWriter(usersFile)) { fw.write("[]"); }

            if (!coursesFile.exists() || coursesFile.length() == 0)
                try (FileWriter fw = new FileWriter(coursesFile)) { fw.write("[]"); }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<User> loadUsers() {
        try (Reader reader = new FileReader(usersFilePath)) {
            List<User> list = gson.fromJson(reader, new TypeToken<List<User>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading users.json → empty list");
            return new ArrayList<>();
        }
    }

    private List<Course> loadCourses() {
        try (Reader reader = new FileReader(coursesFilePath)) {
            List<Course> list = gson.fromJson(reader, new TypeToken<List<Course>>(){}.getType());
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error loading courses.json → empty list");
            return new ArrayList<>();
        }
    }

    /**
     * يربط كل الكائنات ببعضها ويضبط counters
     */
    private void fixRelationshipsAndCounters() {
        // ربط الـ instructor في كل Course
        for (Course c : courses) {
            if (c.getInstructorEmail() != null) {
                Instructor inst = users.stream()
                        .filter(u -> u instanceof Instructor && u.getEmail().equals(c.getInstructorEmail()))
                        .map(u -> (Instructor) u)
                        .findFirst().orElse(null);
                c.setInstructor(inst); // sets instructor AND updates inst.createdCourses
            }

            if (c.getLessons() == null) c.setLessons(new ArrayList<>());
            if (c.getEnrolledStudentIds() == null) c.setEnrolledStudentIds(new ArrayList<>());
            if (c.getStatus() == null) c.setStatus(CourseStatus.PENDING);
        }

        // ربط الطلاب بالكورسات
        for (User u : users) {
            if (u instanceof Student s) {
                if (s.getEnrolledCourseIds() == null) continue;
                for (Integer cid : s.getEnrolledCourseIds()) {
                    Course c = courses.stream().filter(cr -> cr.getCourseId() == cid).findFirst().orElse(null);
                    if (c != null) {
                        if (!c.getEnrolledStudentIds().contains(s.getUserId()))
                            c.getEnrolledStudentIds().add(s.getUserId());
                    }
                }
            }
        }

        // fix counters
        Course.setCourseCounter(courses.stream().mapToInt(Course::getCourseId).max().orElse(0)+1);
        Lesson.setLessonCounter(courses.stream().flatMap(c -> c.getLessons().stream()).mapToInt(Lesson::getLessonId).max().orElse(0)+1);
        User.setUserCounter(users.stream().mapToInt(User::getUserId).max().orElse(0)+1);
    }

    // =================== Save ===================
    public void saveUsers() {
        try (Writer writer = new FileWriter(usersFilePath)) {
            gson.toJson(users, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveCourses() {
        try (Writer writer = new FileWriter(coursesFilePath)) {
            gson.toJson(courses, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }

    // =================== CRUD ===================
    public void addUser(User user) { if(user == null) return; users.add(user); saveUsers(); }
    public void updateUser(User updated) {
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserId() == updated.getUserId()){
                users.set(i, updated);
                saveUsers();
                return;
            }
        }
    }

    public void addCourse(Course course) { if(course == null) return; courses.add(course); saveCourses(); }
    public void updateCourse(Course updated) {
        for(int i=0;i<courses.size();i++){
            if(courses.get(i).getCourseId() == updated.getCourseId()){
                courses.set(i, updated);
                saveCourses();
                return;
            }
        }
    }

    // =================== Getters ===================
    public List<User> getUsers() { return users; }
    public List<Course> getCourses() { return courses; }
}
