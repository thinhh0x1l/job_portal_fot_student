package com.jobportal.dto.dummy;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class DetailStudent {
    String statusIntern;

    String reviewOfRecruiter;

    String studentName;
    String image;
    String mssv;


    String phone = "0987654321";
    String email;
    String gvHd;



    String companyName = "----------";
    String vitriTT ="----------";
    String ntd= "----------";
    String timeTT ="----------";
    String linkCompany= "----------";
    String emailCompany= "----------";
}
