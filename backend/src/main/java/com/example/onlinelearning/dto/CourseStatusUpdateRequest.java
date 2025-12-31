package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.CourseStatus;
import jakarta.validation.constraints.NotNull;

public class CourseStatusUpdateRequest {
    @NotNull
    private CourseStatus status;

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }
}
