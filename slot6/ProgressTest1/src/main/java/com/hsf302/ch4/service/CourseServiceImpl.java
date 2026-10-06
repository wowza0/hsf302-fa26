package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Course;
import com.hsf302.ch4.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    // ===== TODO 6 =====
    @Override
    public long count() {
        return courseRepository.count();
    }

    @Override
    public List<Course> findAllOrderByCode() {
        return courseRepository.findAll(Sort.by("code"));
    }

    @Override
    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    // ===== TODO 8 =====
    @Override
    public Optional<Course> findByCode(String code) {
        return courseRepository.findByCode(code);
    }

    @Override
    public List<Course> findBySemester(String semester) {
        return courseRepository.findBySemesterOrderByCodeAsc(semester);
    }

    @Override
    public long countBySemester(String semester) {
        return courseRepository.countBySemester(semester);
    }

    // ===== TODO 10 =====
    @Override
    public List<Course> findCoursesOfStudent(String studentCode) {
        return courseRepository.findByStudents_StudentCodeOrderByCodeAsc(studentCode);
    }

    @Override
    public List<Course> findCoursesOfDepartment(String deptCode, boolean distinct) {
        if (distinct) {
            return courseRepository.findDistinctByStudents_Department_CodeOrderByCodeAsc(deptCode);
        } else {
            return courseRepository.findByStudents_Department_CodeOrderByCodeAsc(deptCode);
        }
    }

    // ===== TODO 11 =====
    @Override
    public List<Course> findCoursesWithoutStudents() {
        return courseRepository.findByStudentsIsEmpty();
    }

    // ===== Part D =====
    // ===== TODO 13 =====
    @Override
    public List<com.hsf302.ch4.dto.CourseStatDTO> getStatistics() {
        return courseRepository.getCourseStats();
    }

    // ===== TODO 15 =====
    @Override
    public List<Course> findFullCourses() {
        return courseRepository.findFullCourses();
    }

    // ===== TODO 16 =====
    @Override
    public Course getWithStudents(String code) {
        return courseRepository.findWithStudentsByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + code));
    }

    // ===== TODO 17 =====
    @Override
    public List<com.hsf302.ch4.dto.CourseEnrollmentCount> findTopEnrolled(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be > 0");
        }
        return courseRepository.findTopEnrolledNative(n);
    }

    // ===== TODO 23 =====
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void deleteCourseDirectly(String code) {
        Course c = getCourse(code);
        courseRepository.delete(c);
        courseRepository.flush();          // ép Hibernate chạy DELETE ngay để thấy lỗi
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public int deleteCourse(String code) {
        Course c = getCourse(code);
        // copy ra Set mới: unenroll() sẽ sửa c.getStudents() → tránh ConcurrentModificationException
        java.util.Set<com.hsf302.ch4.pojo.Student> students = new java.util.HashSet<>(c.getStudents());
        students.forEach(s -> s.unenroll(c));   // gỡ từ OWNING side → DELETE các dòng student_courses
        courseRepository.delete(c);             // sau đó mới DELETE courses
        return students.size();
    }

    private Course getCourse(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + code));
    }
}
