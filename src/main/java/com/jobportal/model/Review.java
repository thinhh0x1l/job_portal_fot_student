package com.jobportal.model;

public enum Review {
    CHUA_XEP_LOAI("Chưa xếp loại"),
    XUAT_XAC("Xuất sắc"),
    GIOI("Giỏi"),
    KHA("Khá"),
    TRUNG_BINH("Trung bình"),
    YEU("Yếu");

    private final String label;

    Review(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
