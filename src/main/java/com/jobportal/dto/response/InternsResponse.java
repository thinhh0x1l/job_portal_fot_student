package com.jobportal.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@Builder
public class InternsResponse {
    Integer id;
    String name;
    String image;
    String email;
    @Builder.Default
    boolean added = false;
}
