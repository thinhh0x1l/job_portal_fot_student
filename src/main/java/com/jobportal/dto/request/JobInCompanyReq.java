package com.jobportal.dto.request;


import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class JobInCompanyReq {
    Integer companyId;
    Integer page;
}
