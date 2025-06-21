package com.jobportal.model;

public enum TaskStatus {
    DA_HOAN_THANH("Đã hoàn thành"),
    CHUA_HOAN_THANH("Chưa hoàn thành"),
    HOAN_THANH_TRE("Hoàn thành trễ");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
