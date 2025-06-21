package com.jobportal.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PostJobChartResponse {
    int totalCv;
    int totalCvNotApproved;
    int totalCvApproved;
}
