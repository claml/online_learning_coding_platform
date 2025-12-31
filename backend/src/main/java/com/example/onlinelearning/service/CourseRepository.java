package com.example.onlinelearning.service;

import com.example.onlinelearning.entity.Course;
import com.example.onlinelearning.entity.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStatus(CourseStatus status);

    List<Course> findByTeacherId(Long teacherId);
}
