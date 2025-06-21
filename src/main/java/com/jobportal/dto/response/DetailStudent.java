package com.jobportal.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DetailStudent {
    String email;
    String phone;
    String statusInternShip;
    String lecturerName;
    String image;

    String fullname;
    String lastname;
    String firstname;
    String mssv;
    String classAndFaculty;

    String companyName;
    String positionApplied;
    String pointInternShip;
    String reviewOfLecturer;

    int lecturerId;
}
