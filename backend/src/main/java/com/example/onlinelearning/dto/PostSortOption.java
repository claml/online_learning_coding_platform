package com.example.onlinelearning.dto;

public enum PostSortOption {
    LATEST,
    HOTTEST,
    MOST_COMMENTED;

    public static PostSortOption fromString(String value) {
        if (value == null || value.isBlank()) {
            return LATEST;
        }
        try {
            return PostSortOption.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LATEST;
        }
    }
}
