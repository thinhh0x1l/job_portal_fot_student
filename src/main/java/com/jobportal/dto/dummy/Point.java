package com.jobportal.dto.dummy;

import com.jobportal.entity.Intern;
import com.jobportal.entity.Task;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Data
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Point {
    Map<String, List<Task>> map;
    Map<String, Double> point;
    double totalPoint;
    Intern intern;
    int taskCompleted;
    int totalTask;
}
