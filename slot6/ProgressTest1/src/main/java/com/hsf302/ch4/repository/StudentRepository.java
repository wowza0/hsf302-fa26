package com.hsf302.ch4.repository;

import com.hsf302.ch4.dto.StudentSummary;
import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>,
                                           JpaSpecificationExecutor<Student> {

    // ===== TODO 8 =====
    Optional<Student> findByStudentCode(String studentCode);
    boolean existsByEmail(String email);
    long countByActiveTrue();

    // ===== TODO 9 =====
    List<Student> findByFullNameContainingIgnoreCase(String keyword);
    List<Student> findByEmailEndingWith(String suffix);
    List<Student> findByEmailIsNull();

    // ===== TODO 10 =====
    List<Student> findByGpaBetweenOrderByGpaDesc(double min, double max);
    List<Student> findByGenderAndActiveTrue(Gender gender);
    List<Student> findByDobAfter(LocalDate date);

    // ===== TODO 11 =====
    List<Student> findByDepartment_CodeOrderByFullNameAsc(String code);
    long countByDepartment_Code(String code);
    List<Student> findTop3ByOrderByGpaDesc();

    // ===== TODO 12 =====
    @Query("SELECT s FROM Student s " +
           "WHERE s.department.code = :code AND s.gpa >= :minGpa " +
           "ORDER BY s.gpa DESC")
    List<Student> findGoodStudentsInDepartment(@Param("code") String code,
                                               @Param("minGpa") double minGpa);

    // ===== TODO 13 =====
    @Query("SELECT s FROM Student s " +
           "WHERE LOWER(s.fullName) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(s.email)    LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "ORDER BY s.fullName")
    List<Student> searchByKeyword(@Param("kw") String keyword);

    // ===== TODO 15 =====
    @Query("SELECT s FROM Student s " +
           "WHERE s.gpa > (SELECT AVG(s2.gpa) FROM Student s2) " +
           "ORDER BY s.gpa DESC")
    List<Student> findAboveAverageGpa();

    // ===== TODO 17 =====
    @Query(value = "SELECT TOP (:n) s.* " +
                   "FROM students s JOIN departments d ON s.department_id = d.id " +
                   "WHERE d.code = :code " +
                   "ORDER BY s.gpa DESC",
           nativeQuery = true)
    List<Student> findTopNByDepartmentNative(@Param("code") String code, @Param("n") int n);

    // ===== TODO 18 =====
    @Query("SELECT s.studentCode AS studentCode, s.fullName AS fullName, " +
           "       s.gpa AS gpa, d.name AS departmentName " +
           "FROM Student s JOIN s.department d " +
           "WHERE s.active = true " +
           "ORDER BY s.fullName")
    List<StudentSummary> findActiveSummaries();

    // ===== TODO 19 =====
    @Query("SELECT s FROM Student s WHERE s.department.code = :code AND s.active = true")
    Page<Student> findActiveByDepartment(@Param("code") String code, Pageable pageable);

    // ===== TODO 21 =====
    @org.springframework.data.jpa.repository.Modifying
    @Query("UPDATE Student s SET s.active = false WHERE s.gpa < :minGpa")
    int deactivateLowGpaStudents(@Param("minGpa") double minGpa);

    // ===== TODO 23 =====
    @org.springframework.transaction.annotation.Transactional
    long deleteByActiveFalse();

    // ===== Exercise 2 TODO 9 =====
    List<Student> findByCourses_CodeOrderByFullNameAsc(String courseCode);
    long countByCourses_Code(String courseCode);
    List<Student> findByCourses_CodeAndActiveTrueOrderByFullNameAsc(String courseCode);

    // ===== Exercise 2 TODO 11 =====
    List<Student> findByCoursesIsEmptyOrderByFullNameAsc();
    boolean existsByStudentCodeAndCourses_Code(String studentCode, String courseCode);
}
