package com.example.onlinelearning.dto;

import lombok.Data;

@Data
public class BindSubmitRequest {
    /**
     * Legacy binding identifier, kept for compatibility.
     */
    private String identifier;

    /**
     * Student ID for STUDENT role.
     */
    private String studentId;

    /**
     * Teacher ID for TEACHER role.
     */
    private String teacherId;
}
