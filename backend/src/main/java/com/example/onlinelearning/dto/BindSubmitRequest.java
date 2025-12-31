package com.example.onlinelearning.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BindSubmitRequest {
    @NotBlank
    private String identifier;
}
