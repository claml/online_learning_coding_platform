package com.example.onlinelearning.controller;

import com.example.onlinelearning.dto.CourseCreateRequest;
import com.example.onlinelearning.dto.CourseResponse;
import com.example.onlinelearning.dto.CourseStatusUpdateRequest;
import com.example.onlinelearning.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CourseCreateRequest request,
                                                       Authentication authentication) {
        CourseResponse response = courseService.createCourse(request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<CourseResponse>> listAvailableCourses() {
        return ResponseEntity.ok(courseService.getAvailableCourses());
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<CourseResponse>> listTeacherCourses(Authentication authentication) {
        return ResponseEntity.ok(courseService.getTeacherCourses(authentication.getName()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponse> reviewCourse(@PathVariable Long id,
                                                       @Valid @RequestBody CourseStatusUpdateRequest request,
                                                       Authentication authentication) {
        CourseResponse response = courseService.reviewCourse(id, request.getStatus(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/offline")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<CourseResponse> offlineCourse(@PathVariable Long id, Authentication authentication) {
        CourseResponse response = courseService.offlineCourse(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourse(@PathVariable Long id, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(courseService.getCourseForUser(id, username));
    }
}
