package com.jobportal.dto.dummy;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;


@FieldDefaults(level = AccessLevel.PRIVATE)

 @Data
public class RegisterRecruiter {
    String email;
    String password;
    String fullName;
    String companyName;
    String district;
    String phoneNumber;
}
