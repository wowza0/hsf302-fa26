package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.pojo.Student;

import java.util.List;

public interface EnrollmentService {
    List<Course> getCoursesOfStudent(String studentCode);   // TODO 7
    List<Student> getStudentsOfCourse(String courseCode);   // TODO 7

    List<Student> findStudentsInCourse(String courseCode);  // TODO 9
    long countStudentsInCourse(String courseCode);          // TODO 9
    List<Student> findActiveStudentsInCourse(String courseCode); // TODO 9
}
