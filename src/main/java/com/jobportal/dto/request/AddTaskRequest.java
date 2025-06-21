package com.jobportal.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class AddTaskRequest {
    List<Integer> internId;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime endTime;
    String taskName;
    String week;


}
