package com.jobportal.model;

public enum Salary {
    RANGE_0_0_5(0.0, 0.5, "0 - 0.5 triệu"),
    RANGE_0_5_1(0.5, 1.0, "0.5 - 1 triệu"),
    RANGE_1_1_5(1.0, 1.5, "1 - 1.5 triệu"),
    RANGE_1_5_2(1.5, 2.0, "1.5 - 2 triệu"),
    RANGE_2_2_5(2.0, 2.5, "2 - 2.5 triệu"),
    RANGE_2_5_3(2.5, 3.0, "2.5 - 3 triệu"),
    RANGE_3_3_5(3.0, 3.5, "3 - 3.5 triệu"),
    RANGE_3_5_4(3.5, 4.0, "3.5 - 4 triệu"),
    RANGE_4_4_5(4.0, 4.5, "4 - 4.5 triệu"),
    RANGE_4_5_5(4.5, 5.0, "4.5 - 5 triệu"),
    ABOVE_5(5.0, Double.MAX_VALUE, "> 5 triệu");

    private final double min;
    private final double max;
    private final String label;

    Salary(double min, double max, String label) {
        this.min = min;
        this.max = max;
        this.label = label;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public String getLabel() {
        return label;
    }

    public static Salary fromSalary(double salary) {
        for (Salary section : values()) {
            if (salary >= section.min && salary < section.max) {
                return section;
            }
        }
        return null;
    }
}
