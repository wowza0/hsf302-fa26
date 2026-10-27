package com.hsf302.student.controller;

import com.hsf302.student.entity.Student;
import com.hsf302.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Available in the Model of EVERY request in this controller → ${majors} */
    @ModelAttribute("majors")
    public Map<String, String> majors() {
        Map<String, String> majors = new LinkedHashMap<>();
        majors.put("SE", "Kỹ thuật phần mềm");
        majors.put("AI", "Trí tuệ nhân tạo");
        majors.put("IA", "An toàn thông tin");
        majors.put("GD", "Thiết kế đồ họa");
        return majors;
    }

    // TODO 4: GET /students?keyword=...
    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<Student> students = studentService.search(keyword);
        model.addAttribute("students", students);
        model.addAttribute("keyword", keyword);
        model.addAttribute("totalStudents", students.size());
        model.addAttribute("avgGpa", studentService.averageGpa(students));
        model.addAttribute("activeMenu", "list");
        return "students/list";
    }

    // TODO 5: GET /students/{id}
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    model.addAttribute("activeMenu", "list");
                    return "students/detail";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage",
                            "Khong tim thay sinh vien co id = " + id);
                    return "redirect:/students";
                });
    }

    // TODO 6: Display form – create
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("activeMenu", "new");
        return "students/form";
    }

    // TODO 6: Display form – edit
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return studentService.findById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    model.addAttribute("activeMenu", "new");
                    return "students/form";
                })
                .orElseGet(() -> {
                    ra.addFlashAttribute("errorMessage",
                            "Khong tim thay sinh vien co id = " + id);
                    return "redirect:/students";
                });
    }

    // TODO 7: POST /students/save
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("student") Student student,
                       BindingResult result,
                       Model model, RedirectAttributes ra) {
        // Check for duplicate student code
        if (!result.hasFieldErrors("studentCode")
                && studentService.isStudentCodeDuplicated(student.getStudentCode(), student.getId())) {
            result.rejectValue("studentCode", "duplicate", "Ma sinh vien da ton tai");
        }

        if (result.hasErrors()) {
            model.addAttribute("activeMenu", "new");
            return "students/form";
        }

        studentService.save(student);

        String action = (student.getId() == null) ? "Them" : "Cap nhat";
        ra.addFlashAttribute("successMessage",
                action + " sinh vien " + student.getFullName() + " thanh cong!");
        return "redirect:/students";
    }

    // Pre-provided: DELETE
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        studentService.deleteById(id);
        ra.addFlashAttribute("successMessage", "Da xoa sinh vien co id = " + id);
        return "redirect:/students";
    }
}
