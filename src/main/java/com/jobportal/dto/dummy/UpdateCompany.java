package com.jobportal.dto.dummy;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UpdateCompany {
    String description;
    String numberPhone;
    String email;
    String address;
    String companySize;
    String website;
    String companyName;
    String taxCode;
}
