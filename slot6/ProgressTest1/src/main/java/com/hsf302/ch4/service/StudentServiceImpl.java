package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Gender;
import com.hsf302.ch4.pojo.Student;
import com.hsf302.ch4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)          // mặc định: mọi method chỉ ĐỌC
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    // ===== TODO 6 =====
    @Override
    public long count() {
        return studentRepository.count();
    }

    @Override
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    // ===== TODO 7 =====
    @Override
    public List<Student> findAllOrderByGpaDesc() {
        return studentRepository.findAll(Sort.by(Sort.Direction.DESC, "gpa"));
    }

    @Override
    public Page<Student> findPage(int pageIndex, int size, String sortField) {
        if (pageIndex < 0 || size <= 0) {
            throw new IllegalArgumentException("pageIndex phải >= 0 và size phải > 0");
        }
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(sortField).ascending());
        return studentRepository.findAll(pageable);
    }

    // ===== TODO 8 =====
    @Override
    public Optional<Student> findByStudentCode(String studentCode) {
        return studentRepository.findByStudentCode(studentCode);
    }

    @Override
    public boolean isEmailExisted(String email) {
        return studentRepository.existsByEmail(email);
    }

    @Override
    public long countActive() {
        return studentRepository.countByActiveTrue();
    }

    // ===== TODO 9 =====
    @Override
    public List<Student> searchByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return studentRepository.findByFullNameContainingIgnoreCase(keyword.trim());
    }

    @Override
    public List<Student> findByEmailDomain(String domain) {
        String suffix = domain.startsWith("@") ? domain : "@" + domain;
        return studentRepository.findByEmailEndingWith(suffix);
    }

    @Override
    public List<Student> findWithoutEmail() {
        return studentRepository.findByEmailIsNull();
    }

    // ===== TODO 10 =====
    @Override
    public List<Student> findByGpaRange(double min, double max) {
        if (min > max) {
            throw new IllegalArgumentException("min GPA phải <= max GPA");
        }
        return studentRepository.findByGpaBetweenOrderByGpaDesc(min, max);
    }

    @Override
    public List<Student> findActiveByGender(Gender gender) {
        return studentRepository.findByGenderAndActiveTrue(gender);
    }

    @Override
    public List<Student> findBornAfter(LocalDate date) {
        return studentRepository.findByDobAfter(date);
    }

    // ===== TODO 11 =====
    @Override
    public List<Student> findByDepartment(String deptCode) {
        return studentRepository.findByDepartment_CodeOrderByFullNameAsc(deptCode);
    }

    @Override
    public long countByDepartment(String deptCode) {
        return studentRepository.countByDepartment_Code(deptCode);
    }

    @Override
    public List<Student> findTop3ByGpa() {
        return studentRepository.findTop3ByOrderByGpaDesc();
    }

    // ===== TODO 12 =====
    @Override
    public List<Student> findGoodStudents(String deptCode, double minGpa) {
        return studentRepository.findGoodStudentsInDepartment(deptCode, minGpa);
    }

    // ===== TODO 13 =====
    @Override
    public List<Student> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return studentRepository.searchByKeyword(keyword.trim());
    }

    // ===== TODO 15 =====
    @Override
    public List<Student> findAboveAverageGpa() {
        return studentRepository.findAboveAverageGpa();
    }

    // ===== TODO 17 =====
    @Override
    public List<Student> findTopNInDepartment(String deptCode, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n phải > 0");
        }
        return studentRepository.findTopNByDepartmentNative(deptCode, n);
    }

    // ===== TODO 18 =====
    @Override
    public List<com.hsf302.ch4.dto.StudentSummary> getActiveSummaries() {
        return studentRepository.findActiveSummaries();
    }

    // ===== TODO 19 =====
    @Override
    public Page<Student> findActiveByDepartment(String deptCode, int pageIndex, int size) {
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("gpa").descending());
        return studentRepository.findActiveByDepartment(deptCode, pageable);
    }

    // ===== TODO 24 =====
    @Override
    public List<Student> search(String kw, String deptCode, Double minGpa, Boolean active) {
        org.springframework.data.jpa.domain.Specification<Student> spec =
                org.springframework.data.jpa.domain.Specification
                        .where(com.hsf302.ch4.specification.StudentSpecs.nameContains(kw))
                        .and(com.hsf302.ch4.specification.StudentSpecs.inDepartment(deptCode))
                        .and(com.hsf302.ch4.specification.StudentSpecs.gpaAtLeast(minGpa))
                        .and(com.hsf302.ch4.specification.StudentSpecs.isActive(active));
        return studentRepository.findAll(spec, Sort.by("fullName"));
    }

    // ===== TODO 20 =====
    @Override
    @Transactional // Quan trọng: cần Transaction để ghi đè dữ liệu, vì class đang là readOnly = true
    public void updateStudentGpa(Long id, double newGpa) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        student.setGpa(newGpa); // Hibernate tự update (Dirty Checking) khi transaction commit
    }

    // ===== TODO 21 =====
    @Override
    @Transactional // UPDATE bắt buộc phải có Transaction
    public int deactivateLowGpaStudents(double minGpa) {
        return studentRepository.deactivateLowGpaStudents(minGpa);
    }

    // ===== TODO 23 =====
    @Override
    @Transactional
    public long deleteInactiveStudents() {
        return studentRepository.deleteByActiveFalse();
    }
}