package com.jobportal.model;

public enum Faculty {
    SOFTWARE_ENGINEERING("Công nghệ phần mềm"),
    INFORMATION_SECURITY("An toàn thông tin");

    private final String displayName;

    Faculty(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
