package com.example.onlinelearning.service;

import com.example.onlinelearning.dto.*;
import com.example.onlinelearning.entity.*;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request, String username) {
        User teacher = findUserOrThrow(username);
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only teachers can create courses");
        }
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .status(CourseStatus.PENDING_REVIEW)
                .chapters(request.getChapters().stream()
                        .map(this::toChapter)
                        .collect(Collectors.toList()))
                .build();
        courseRepository.save(course);
        return toResponse(course);
    }

    public List<CourseResponse> getAvailableCourses() {
        return courseRepository.findByStatus(CourseStatus.AVAILABLE).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CourseResponse> getTeacherCourses(String username) {
        User teacher = findUserOrThrow(username);
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only teachers can query their courses");
        }
        return courseRepository.findByTeacherId(teacher.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CourseResponse reviewCourse(Long courseId, CourseStatus status, String username) {
        Assert.notNull(status, "status is required");
        User admin = findUserOrThrow(username);
        if (admin.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admins can review courses");
        }
        if (status != CourseStatus.AVAILABLE && status != CourseStatus.OFFLINE) {
            throw new IllegalArgumentException("Status must be AVAILABLE or OFFLINE when reviewing");
        }
        Course course = findCourseOrThrow(courseId);
        course.setStatus(status);
        return toResponse(course);
    }

    @Transactional
    public CourseResponse offlineCourse(Long courseId, String username) {
        User teacher = findUserOrThrow(username);
        if (teacher.getRole() != Role.TEACHER) {
            throw new AccessDeniedException("Only teachers can offline their courses");
        }
        Course course = findCourseOrThrow(courseId);
        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new AccessDeniedException("You can only offline your own courses");
        }
        course.setStatus(CourseStatus.OFFLINE);
        return toResponse(course);
    }

    public CourseResponse getCourseForUser(Long courseId, String username) {
        Course course = findCourseOrThrow(courseId);
        Optional<User> userOpt = username == null ? Optional.empty() : userRepository.findByUsername(username);
        if (course.getStatus() == CourseStatus.AVAILABLE) {
            return toResponse(course);
        }
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getRole() == Role.ADMIN) {
                return toResponse(course);
            }
            if (user.getRole() == Role.TEACHER && course.getTeacher().getId().equals(user.getId())) {
                return toResponse(course);
            }
        }
        throw new AccessDeniedException("Course is not available");
    }

    private User findUserOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Course findCourseOrThrow(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    private Chapter toChapter(ChapterRequest request) {
        return Chapter.builder()
                .title(request.getTitle())
                .videoUrl(request.getVideoUrl())
                .question(request.getQuestion())
                .build();
    }

    private List<ChapterResponse> toChapterResponses(Course course) {
        return course.getChapters().stream()
                .map(chapter -> new ChapterResponse(chapter.getTitle(), chapter.getVideoUrl(), chapter.getQuestion()))
                .toList();
    }

    private CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getTeacher().getId(),
                course.getTeacher().getUsername(),
                course.getStatus(),
                toChapterResponses(course)
        );
    }
}
