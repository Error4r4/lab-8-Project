package com.example.ui;

import com.example.models.Course;
import com.example.models.Student;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ChartFrame extends JFrame {

    public ChartFrame(Course course) {
        setTitle("Insights for " + course.getTitle());
        setSize(800, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        //Student Performance
        DefaultCategoryDataset performanceDataset = new DefaultCategoryDataset();
        for(Student s : course.getEnrolledStudents()){
            int progress = s.getProgress(course); 
            performanceDataset.addValue(progress, s.getUsername(), "Progress");
        }
        JFreeChart performanceChart = ChartFactory.createBarChart(
                "Student Progress",
                "Student",
                "Completion %",
                performanceDataset
        );
        tabbedPane.add("Student Progress", new ChartPanel(performanceChart));

        //Quiz Averages
        DefaultCategoryDataset quizDataset = new DefaultCategoryDataset();
        course.getLessons().forEach(lesson -> {
            double avg = lesson.getQuizAverage();
            quizDataset.addValue(avg, "Average", lesson.getTitle());
        });
        JFreeChart quizChart = ChartFactory.createLineChart(
                "Quiz Averages",
                "Lesson",
                "Average Score",
                quizDataset
        );
        tabbedPane.add("Quiz Averages", new ChartPanel(quizChart));

        //Completion Percentages
        DefaultCategoryDataset completionDataset = new DefaultCategoryDataset();
        course.getLessons().forEach(lesson -> {
            int completed = (int) course.getEnrolledStudents().stream()
                    .filter(s -> s.hasCompletedLesson(lesson))
                    .count();
            int total = course.getEnrolledStudents().size();
            double percent = total > 0 ? (completed * 100.0 / total) : 0;
            completionDataset.addValue(percent, "Completion %", lesson.getTitle());
        });
        JFreeChart completionChart = ChartFactory.createBarChart(
                "Lesson Completion %",
                "Lesson",
                "Completion %",
                completionDataset
        );
        tabbedPane.add("Completion %", new ChartPanel(completionChart));

        add(tabbedPane, BorderLayout.CENTER);
        setVisible(true);
    }
}
