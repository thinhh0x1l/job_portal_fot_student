package com.jobportal.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class JobPostRequest {
    private String name;
    private String major;
    private String description;
    private String candidateRequirements;
    private double salary;
    private LocalDate postingDeadline;
    private String benefits;
    private boolean urgent;
    private int numberOfApplications;
    private Set<Integer> tagIds;
}
