package com.jobportal.model;

public enum CompanySize {
    UNDER_10("1-9 nhân viên",1),
    FROM_10_TO_24("10-24 nhân viên",2),
    FROM_25_TO_49("25-49 nhân viên",3),
    FROM_50_TO_99("50-99 nhân viên",4),
    FROM_100_TO_499("100-499 nhân viên",5),
    FROM_500_TO_999("500-999 nhân viên",6),
    FROM_1000_TO_2999("1000+ nhân viên",7),
    FROM_3000_TO_4999("3000+ nhân viên",8),
    FROM_5000_UP("5000+ nhân viên",9);

    private final String displayName;
    private final int value;
    CompanySize(String displayName, int size) {
        this.displayName = displayName;
        this.value = size;
    }

    public String getDisplayName() {
        return displayName;
    }
    public int getValue() {
        return value;
    }
}
