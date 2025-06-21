package com.jobportal.model;

public enum TagType {
    PROGRAMMING_LANGUAGE("Ngôn ngữ lập trình"),
    BACKEND("Backend"),
    FRONTEND("Frontend"),
    DATABASE("Database"),
    OTHER("Khác");

    private final String displayName;

    TagType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
