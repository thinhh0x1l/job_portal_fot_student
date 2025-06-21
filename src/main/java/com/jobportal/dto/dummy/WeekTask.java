package com.jobportal.dto.dummy;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class WeekTask {
    int weekOrder;
    List<String> titles;
    List<Integer> status;
    List<Task> tasks = new ArrayList<>();


    List<Double> dc1;
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Task{
        int taskId;
        String reviewCompany;
        String pointCompany;

        String description;
        String report;

        String title;
        int status;
        String time;
    }

}
