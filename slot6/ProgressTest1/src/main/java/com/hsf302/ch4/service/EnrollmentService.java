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

    List<Student> findStudentsWithoutCourses();             // TODO 11
    boolean isEnrolled(String studentCode, String courseCode); // TODO 11

    // ===== Part D =====
    List<Student> findGoodStudentsInCourse(String courseCode, double minGpa);              // TODO 12
    List<com.hsf302.ch4.dto.StudentCreditDTO> getCreditSummary(int minCredits);          // TODO 14
    List<Student> findStudentsWithMoreThan(int n);                                         // TODO 15
    Student getStudentWithCourses(String studentCode);                                     // TODO 16
    List<com.hsf302.ch4.dto.EnrollmentView> getEnrollmentsOfDepartment(String deptCode);                      // TODO 18
    org.springframework.data.domain.Page<Student> findStudentsInCoursePage(String courseCode, int pageIndex, int size);    // TODO 19

    // ===== Bonus =====
    List<Student> search(String courseCode, String semester, String deptCode, Double minGpa); // TODO 25
}
