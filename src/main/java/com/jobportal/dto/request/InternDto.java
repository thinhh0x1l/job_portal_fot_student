package com.jobportal.dto.request;


import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class InternDto {
    Integer id;
    String email;
    String password;
    String firstName;
    String lastName;
}
