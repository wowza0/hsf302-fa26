package com.hsf302.ch4.service;

import com.hsf302.ch4.dto.DepartmentStatDTO;
import com.hsf302.ch4.pojo.Department;
import com.hsf302.ch4.repository.DepartmentRepository;
import com.hsf302.ch4.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final StudentRepository studentRepository;      // dùng ở TODO 22 (chuyển sinh viên)

    // ===== TODO 6 =====
    @Override
    public long count() {
        return departmentRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return departmentRepository.existsById(id);
    }

    // ===== TODO 11 =====
    @Override
    public List<Department> findDepartmentsWithoutStudents() {
        return departmentRepository.findByStudentsIsEmpty();
    }

    // ===== TODO 14 =====
    @Override
    public List<DepartmentStatDTO> getStatistics() {
        return departmentRepository.getDepartmentStats();
    }

    // ===== TODO 16 =====
    @Override
    public Optional<Department> findByCode(String code) {
        return departmentRepository.findByCode(code);
    }

    @Override
    public Department getWithStudents(String code) {
        return departmentRepository.findByCodeWithStudents(code)
                .orElseThrow(() -> new IllegalArgumentException("Department not found: " + code));
    }

    // ===== TODO 22 =====
    @Override
    @Transactional
    public void deleteDepartmentAndMoveStudents(String fromCode, String toCode) {
        // Lấy phòng ban cũ (cùng với ds sinh viên để chuẩn bị dời đi)
        Department fromDept = getWithStudents(fromCode);
        // Lấy phòng ban mới
        Department toDept = departmentRepository.findByCode(toCode)
                .orElseThrow(() -> new IllegalArgumentException("Target department not found: " + toCode));

        // Chuyển từng sinh viên sang khoa mới
        for (com.hsf302.ch4.pojo.Student s : fromDept.getStudents()) {
            s.setDepartment(toDept);
        }
        // Lưu lại danh sách sinh viên đã chuyển khoa (cần thiết tùy vào cascade)
        studentRepository.saveAll(fromDept.getStudents());

        // Xoá khoa cũ (phải clear list students để không bị ràng buộc nếu có cascade)
        fromDept.getStudents().clear();
        departmentRepository.delete(fromDept);
    }
}
