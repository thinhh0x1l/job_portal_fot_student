package com.jobportal.dto.request;

import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostJobUpdate {
    Integer id;
    String name;
    String description;
    double salary;
    String candidateRequirements;
    String benefits;
    boolean urgent;
    String major;
    Set<Integer> tagIds;
}
