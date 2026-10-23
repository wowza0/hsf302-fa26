package com.hsf302.student.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.Period;

// TODO 18: Ánh xạ entity với bảng SQL Server
@Entity
@Table(name = "Students")
public class Student {

    // id: @Id + @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO 1: Validation cho studentCode, fullName, email
    // studentCode: bắt buộc; regex ^(SE|SS|SA|HE)\d{6}$
    @NotBlank(message = "Mã sinh viên không được để trống")
    @Pattern(regexp = "^(SE|SS|SA|HE)\\d{6}$",
             message = "Mã SV phải có dạng SE/SS/SA/HE + 6 chữ số (VD: SE180001)")
    @Column(name = "student_code", nullable = false, length = 8, unique = true)
    private String studentCode;

    // fullName: bắt buộc; độ dài 2 – 50; NVARCHAR để lưu tiếng Việt
    @NotBlank(message = "Họ tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    @Nationalized
    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    // email: bắt buộc; đúng định dạng email
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // TODO 2: Validation cho dateOfBirth, gpa, major
    // dateOfBirth: bắt buộc; phải là ngày trong quá khứ
    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    // gpa: bắt buộc; 0.0 <= gpa <= 10.0
    @NotNull(message = "GPA không được để trống")
    @DecimalMin(value = "0.0", message = "GPA phải >= 0.0")
    @DecimalMax(value = "10.0", message = "GPA phải <= 10.0")
    @Column(name = "gpa", nullable = false)
    private Double gpa;

    // major: bắt buộc (chọn ngành)
    @NotBlank(message = "Vui lòng chọn ngành học")
    @Column(name = "major", nullable = false, length = 2)
    private String major;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    // TODO 0: Constructor không tham số (bắt buộc cho JPA)
    public Student() {
    }

    // Constructor đầy đủ tham số
    public Student(Long id, String studentCode, String fullName, String email,
                   LocalDate dateOfBirth, Double gpa, String major, boolean active) {
        this.id = id;
        this.studentCode = studentCode;
        this.fullName = fullName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.gpa = gpa;
        this.major = major;
        this.active = active;
    }

    // TODO 3: Thuộc tính tính toán (computed properties) – không có field tương ứng

    /**
     * Tính tuổi từ dateOfBirth.
     * dateOfBirth == null → trả về 0.
     */
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Xếp loại học lực theo GPA:
     *   >= 9.0  → Xuất sắc
     *   >= 8.0  → Giỏi
     *   >= 6.5  → Khá
     *   >= 5.0  → Trung bình
     *   còn lại → Yếu
     *   gpa == null → Chưa xếp loại
     */
    public String getRank() {
        if (gpa == null) return "Chưa xếp loại";
        if (gpa >= 9.0) return "Xuất sắc";
        if (gpa >= 8.0) return "Giỏi";
        if (gpa >= 6.5) return "Khá";
        if (gpa >= 5.0) return "Trung bình";
        return "Yếu";
    }

    // TODO 0: Getters & Setters cho tất cả field
    // field boolean "active" → getter là isActive()

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getGpa() {
        return gpa;
    }

    public void setGpa(Double gpa) {
        this.gpa = gpa;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
