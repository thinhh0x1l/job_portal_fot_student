package com.jobportal.model;


public enum InternshipStatus {
    NOT_PRACTICED("Chưa thực tập"),
    IS_PRACTICING("Đang thực tập"),
    COMPLETED("Hoàn thành thực tập");

    private final String label;

    InternshipStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
