package com.example.onlinelearning.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CourseCreateRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotEmpty
    @Valid
    private List<ChapterRequest> chapters;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ChapterRequest> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterRequest> chapters) {
        this.chapters = chapters;
    }
}
