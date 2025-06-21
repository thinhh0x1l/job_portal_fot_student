package com.jobportal.model;

public enum Major {
    BACKEND("Lập trình Backend","Backend"),
    FRONTEND("Lập trình Frontend","Frontend"),
    MACHINE_LEARNING("Machine Learning","Machine Learning"),
    ANDROID("Lập trình Android","Android"),
    SECURITY_ANALYST("Phân tích bảo mật","Security Analyst"),
    PENETRATION_TESTING("Kiểm thử xâm nhập","Penetration Testing"),
//    DATA_ANALYST("Phân tích dữ liệu"),
//    TESTER("Kiểm thử phần mềm"),
//    GAME_DEVELOPER("Lập trình game"),
//    UI_UX_DESIGNER("Thiết kế UI/UX")
    ;

    private final String label;
    private final String label2;

    Major(String label, String label2) {
        this.label = label;
        this.label2 = label2;
    }

    public String getLabel() {
        return label;
    }
    public String getLabe2l() {
        return label2;
    }
}
