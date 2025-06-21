package com.jobportal.dto.request;

import lombok.Data;

@Data
public class SendOutCvDto {
    private String email;
    private Integer jobId;
    private String fullname;
    private String fileCvUrl;
    private String phoneNumber;
    private String introduction;
    // Getters và Setters
}