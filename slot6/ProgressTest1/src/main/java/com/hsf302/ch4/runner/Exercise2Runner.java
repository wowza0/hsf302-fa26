package com.hsf302.ch4.runner;

import com.hsf302.ch4.service.CourseService;
import com.hsf302.ch4.service.EnrollmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Order(3)
@Profile("ex2")
@RequiredArgsConstructor
public class Exercise2Runner implements CommandLineRunner {

    // CHỈ inject Service interface
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;          // của Exercise 1 (dùng ở TODO 16a, 21)

    @Override
    public void run(String... args) {
        partB();
        partC();
        // partD(); partE() sẽ thêm dần
    }

    private void partB() { todo6(); todo7(); }
    private void partC() { todo8(); todo9(); todo10(); }

    // ===== helpers =====
    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }

    /** Chạy 1 thao tác ghi, in [OK] hoặc [FAIL] + message (dùng cho Part E). */
    private void attempt(String label, Runnable action) {
        try {
            action.run();
            System.out.println("   [OK]   " + label);
        } catch (RuntimeException e) {
            System.out.println("   [FAIL] " + label + " -> " + e.getMessage());
        }
    }

    // todo6() ... todo10() viết ở các TODO bên dưới
    // ===== TODO 6 =====
    private void todo6() {
        title("TODO 6: count, findAll(Sort), findById");
        System.out.println("Total courses: " + courseService.count());
        printList("All courses order by code", courseService.findAllOrderByCode());
        for (long id : new long[]{2L, 99L}) {
            System.out.println("findById(" + id + "): "
                    + courseService.findById(id).map(com.hsf302.ch4.pojo.Course::toString).orElse("Not found"));
        }
    }
    // ===== TODO 7 =====
    private void todo7() {
        title("TODO 7: navigate student.getCourses() / course.getStudents()");
        printList("(a) Courses of SE001", enrollmentService.getCoursesOfStudent("SE001"));
        printList("(b) Students of AIL303", enrollmentService.getStudentsOfCourse("AIL303"));
    }
    // ===== TODO 8 =====
    private void todo8() {
        title("TODO 8: findByCode, findBySemester, countBySemester");
        for (String code : java.util.List.of("HSF302", "XXX000")) {
            System.out.println("(a) " + code + ": "
                    + courseService.findByCode(code).map(com.hsf302.ch4.pojo.Course::getName).orElse("Not found"));
        }
        printList("(b) Semester SU26", courseService.findBySemester("SU26"));
        System.out.println("(c) Courses in FA26: " + courseService.countBySemester("FA26"));
    }
    // ===== TODO 9 =====
    private void todo9() {
        title("TODO 9: derived query through collection courses");
        printList("(a) Students of PRJ301", enrollmentService.findStudentsInCourse("PRJ301"));
        System.out.println("(b) Students of HSF302: " + enrollmentService.countStudentsInCourse("HSF302"));
        printList("(c) Active students of PRJ301", enrollmentService.findActiveStudentsInCourse("PRJ301"));
    }
    // ===== TODO 10 =====
    private void todo10() {
        title("TODO 10: derived query from inverse side, Distinct");
        printList("(a) Courses of SE002", courseService.findCoursesOfStudent("SE002"));
        printList("(b1) Courses of AI students - no Distinct", courseService.findCoursesOfDepartment("AI", false));
        printList("(b2) Courses of AI students - Distinct", courseService.findCoursesOfDepartment("AI", true));
    }
}
