package com.jobportal.dto.response;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompanyRes {
    int id;
    String companyName;
    String companyWebsite;
    String companySize;
    String district;
    String image;

    String description;

    String companyPhone;
    String companyEmail;
    String companyAddress;
    String taxCode;

    String certificate;

    String checkInfor;
    String checkCer;
}
