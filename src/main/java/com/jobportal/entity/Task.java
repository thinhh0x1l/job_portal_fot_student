package com.jobportal.entity;

import com.jobportal.model.SecWeeks;
import com.jobportal.model.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "intern_id")
    Intern intern;

    @ManyToOne
    @JoinColumn(name = "recruiter_id")
    Recruiter recruiter ;




    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime submitTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime timePost;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    TaskStatus taskStatus;

    @Enumerated(EnumType.STRING)
    SecWeeks weeks;
    int weekOrder;
    String name;
    String task;

    // submit pdf
    String report;
    String description;

    // evalutes recruiter
    Double points;
    String review;

    // evalutes lecturer
    Double pointsLecture;
    String reviewLecture;

    public void setIntern(Intern intern) {
        this.intern = intern;
        intern.getTasks().add(this);
    }
    public void setPostJob(Recruiter recruiter) {
        this.recruiter = recruiter;
        recruiter.getTasks().add(this);
    }
}
