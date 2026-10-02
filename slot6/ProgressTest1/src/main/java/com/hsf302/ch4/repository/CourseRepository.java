package com.hsf302.ch4.repository;

import com.hsf302.ch4.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByCode(String code);   // TODO 7 (dùng lại ở TODO 8 và Part E)
    List<Course> findBySemesterOrderByCodeAsc(String semester);
    long countBySemester(String semester);

    // ===== Exercise 2 TODO 10 =====
    List<Course> findByStudents_StudentCodeOrderByCodeAsc(String studentCode);
    List<Course> findByStudents_Department_CodeOrderByCodeAsc(String deptCode);
    List<Course> findDistinctByStudents_Department_CodeOrderByCodeAsc(String deptCode);
}
