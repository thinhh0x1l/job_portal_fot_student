package com.jobportal.model;

public enum SecWeeks {
    TUAN_1("Tuần 1", 1),
    TUAN_2("Tuần 2", 2),
    TUAN_3("Tuần 3", 3),
    TUAN_4("Tuần 4", 4),
    TUAN_5("Tuần 5", 5),
    TUAN_6("Tuần 6", 6),
    TUAN_7("Tuần 7", 7),
    TUAN_8("Tuần 8", 8),
    TUAN_9("Tuần 9", 9),
    TUAN_10("Tuần 10", 10);

    private final String label;
    private final int order;

    SecWeeks(String label, int order) {
        this.label = label;
        this.order = order;
    }

    public String label() {
        return label;
    }

    public int getOrder() {
        return order;
    }
}

