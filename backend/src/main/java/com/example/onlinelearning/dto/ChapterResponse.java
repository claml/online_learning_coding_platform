package com.example.onlinelearning.dto;

public class ChapterResponse {
    private String title;
    private String videoUrl;
    private String question;

    public ChapterResponse(String title, String videoUrl, String question) {
        this.title = title;
        this.videoUrl = videoUrl;
        this.question = question;
    }

    public String getTitle() {
        return title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getQuestion() {
        return question;
    }
}
