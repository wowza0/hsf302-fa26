package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    long count();                               // TODO 6
    List<Course> findAllOrderByCode();          // TODO 6
    Optional<Course> findById(Long id);         // TODO 6

    Optional<Course> findByCode(String code);   // TODO 8
    List<Course> findBySemester(String semester); // TODO 8
    long countBySemester(String semester);      // TODO 8

    List<Course> findCoursesOfStudent(String studentCode);               // TODO 10
    List<Course> findCoursesOfDepartment(String deptCode, boolean distinct); // TODO 10
    List<Course> findCoursesWithoutStudents();                           // TODO 11

    // ===== Part D =====
    List<com.hsf302.ch4.dto.CourseStatDTO> getStatistics();              // TODO 13
    List<Course> findFullCourses();                                      // TODO 15
}
