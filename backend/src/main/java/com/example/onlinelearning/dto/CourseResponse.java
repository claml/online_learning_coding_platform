package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.CourseStatus;

import java.util.List;

public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private Long teacherId;
    private String teacherUsername;
    private CourseStatus status;
    private List<ChapterResponse> chapters;

    public CourseResponse(Long id, String title, String description, Long teacherId, String teacherUsername, CourseStatus status, List<ChapterResponse> chapters) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.teacherId = teacherId;
        this.teacherUsername = teacherUsername;
        this.status = status;
        this.chapters = chapters;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public String getTeacherUsername() {
        return teacherUsername;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public List<ChapterResponse> getChapters() {
        return chapters;
    }
}
