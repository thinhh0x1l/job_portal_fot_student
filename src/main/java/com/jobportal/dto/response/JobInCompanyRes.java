package com.jobportal.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JobInCompanyRes {
    int currentPage;
    int totalPages;
    long elementStart;
    long elementEnd;
    long totalElements;

    List<Job> jobs = new ArrayList<>();

    @Data
   public class Job{
       Integer id;
       String name;
       Set<Tags> tags = new HashSet<>();
       LocalDateTime createdAt;
       String salary;
       int views;
       Boolean favorite;



       @Data
       @AllArgsConstructor
       public class Tags{
           int id;
           String name;
           String className;
       }
   }
}
