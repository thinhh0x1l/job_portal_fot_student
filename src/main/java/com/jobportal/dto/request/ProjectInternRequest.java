package com.jobportal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProjectInternRequest {
    Integer projectId;
    String projectName;
    String projectDescription;
    String linkGit;

}
