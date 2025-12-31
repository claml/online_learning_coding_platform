package com.example.onlinelearning.dto;

import jakarta.validation.constraints.NotBlank;

public class ChapterRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String videoUrl;

    @NotBlank
    private String question;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
