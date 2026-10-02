package com.hsf302.ch4.service;

import com.hsf302.ch4.pojo.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    long count();                               // TODO 6
    List<Course> findAllOrderByCode();          // TODO 6
    Optional<Course> findById(Long id);         // TODO 6
}
