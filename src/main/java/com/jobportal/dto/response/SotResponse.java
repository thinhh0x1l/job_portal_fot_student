package com.jobportal.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Builder
public class SotResponse {
    Integer id;
    String sotUrl;
    String companyImage;
    String content;
    String companyName;
    String jobName;
    String jobUrl;
    String statusCv;

}
