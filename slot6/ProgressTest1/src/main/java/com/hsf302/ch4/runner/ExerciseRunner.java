package com.hsf302.ch4.runner;

import com.hsf302.ch4.service.DepartmentService;
import com.hsf302.ch4.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Order(2)
@RequiredArgsConstructor
public class ExerciseRunner implements CommandLineRunner {

    // Runner CHỈ phụ thuộc vào Service (interface), KHÔNG inject Repository
    private final DepartmentService departmentService;
    private final StudentService studentService;

    @Override
    public void run(String... args) {
        partB();
        partC();
        partD();
        bonus();      // chạy trên dữ liệu gốc → trước Part E
        partE();
    }

    private void partB() { todo6(); todo7(); }
    private void partC() { todo8(); todo9(); todo10(); todo11(); }
    private void partD() { todo12(); todo13(); todo14(); todo15(); todo16(); todo17(); todo18(); todo19(); }
    private void bonus() { todo24(); }
    private void partE() { todo20(); todo21(); todo22(); todo23(); }

    // ===== helpers =====
    private void title(String t) {
        System.out.println("\n===== " + t + " =====");
    }

    private void printList(String label, Collection<?> list) {
        System.out.println("-- " + label + ":");
        list.forEach(o -> System.out.println("   " + o));
        System.out.println("   -> " + list.size() + " record(s)");
    }

    // todo6() ... todo24() viết ở các TODO bên dưới
    private void todo6() {}
    private void todo7() {}
    private void todo8() {}
    private void todo9() {}
    private void todo10() {}
    private void todo11() {}
    private void todo12() {}
    private void todo13() {}
    private void todo14() {}
    private void todo15() {}
    private void todo16() {}
    private void todo17() {}
    private void todo18() {}
    private void todo19() {}
    private void todo20() {}
    private void todo21() {}
    private void todo22() {}
    private void todo23() {}
    private void todo24() {}
}
