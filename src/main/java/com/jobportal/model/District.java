package com.jobportal.model;

public enum District {
    QUAN_1("Quận 1"),
    QUAN_3("Quận 3"),
    QUAN_4("Quận 4"),
    QUAN_5("Quận 5"),
    QUAN_6("Quận 6"),
    QUAN_7("Quận 7"),
    QUAN_8("Quận 8"),
    QUAN_10("Quận 10"),
    QUAN_11("Quận 11"),
    QUAN_12("Quận 12"),
    QUAN_BINH_THANH("Quận Bình Thạnh"),
    QUAN_TAN_BINH("Quận Tân Bình"),
    QUAN_TAN_PHU("Quận Tân Phú"),
    QUAN_PHU_NHUAN("Quận Phú Nhuận"),
    QUAN_GO_VAP("Quận Gò Vấp"),
    THANH_PHO_THU_DUC("Thành phố Thủ Đức"),
    HUYEN_BINH_CHANH("Huyện Bình Chánh"),
    HUYEN_CU_CHI("Huyện Củ Chi"),
    HUYEN_HOC_MON("Huyện Hóc Môn"),
    HUYEN_NHA_BE("Huyện Nhà Bè"),
    HUYEN_CAN_GIO("Huyện Cần Giờ");

    private final String displayName;

    District(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    // Optional: Tìm enum từ chuỗi hiển thị
    public static District fromDisplayName(String displayName) {
        for (District location : values()) {
            if (location.displayName.equalsIgnoreCase(displayName)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy location: " + displayName);
    }
}
