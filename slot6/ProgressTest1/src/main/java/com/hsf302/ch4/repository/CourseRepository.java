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

    // ===== Exercise 2 TODO 11 =====
    List<Course> findByStudentsIsEmpty();

    // ===== Exercise 2 TODO 13 =====
    @org.springframework.data.jpa.repository.Query("SELECT new com.hsf302.ch4.dto.CourseStatDTO(c.code, c.name, c.capacity, COUNT(s), AVG(s.gpa)) " +
           "FROM Course c LEFT JOIN c.students s " +
           "GROUP BY c.code, c.name, c.capacity ORDER BY c.code")
    List<com.hsf302.ch4.dto.CourseStatDTO> getCourseStats();

    // ===== Exercise 2 TODO 15 =====
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Course c WHERE SIZE(c.students) >= c.capacity ORDER BY c.code")
    List<Course> findFullCourses();

    // ===== Exercise 2 TODO 16 =====
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = "students")
    Optional<Course> findWithStudentsByCode(String code);

    // ===== Exercise 2 TODO 17 =====
    @org.springframework.data.jpa.repository.Query(value = "SELECT TOP (:n) c.code AS code, c.name AS name, COUNT(sc.student_id) AS enrolled " +
                   "FROM courses c LEFT JOIN student_courses sc ON sc.course_id = c.id " +
                   "GROUP BY c.code, c.name " +
                   "ORDER BY enrolled DESC, c.code",
           nativeQuery = true)
    List<com.hsf302.ch4.dto.CourseEnrollmentCount> findTopEnrolledNative(@org.springframework.data.repository.query.Param("n") int n);
}
