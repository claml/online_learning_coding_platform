package com.example.onlinelearning.dto;

import com.example.onlinelearning.entity.CourseStatus;
import jakarta.validation.constraints.NotNull;

public class CourseStatusUpdateRequest {
    @NotNull
    private CourseStatus status;

    private String reason;

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
