package com.jobportal.dto.request;


import com.jobportal.dto.response.CompaniesRes;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CompaniesReq {
    int page;

    String keywords;
    String filter; /// 0-default , 1-Dg^, 2-Dg<, 3-J^, 4-J<
    String districtCode;
}
