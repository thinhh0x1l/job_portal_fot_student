package com.jobportal.entity;


import com.jobportal.model.AuthProvider;
import com.jobportal.model.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Recruiters")
public class Recruiter extends User{


    @OneToOne()
    Company company;

    /// Giao tasks
    @Builder.Default
    @OneToMany(mappedBy = "recruiter",fetch = FetchType.EAGER)
    List<Task> tasks = new ArrayList<>();

    /// Đăng tuyển
    @Builder.Default
    @OneToMany(mappedBy = "recruiter",fetch = FetchType.EAGER)
    List<PostJob> postJobs = new ArrayList<>();

    /// Tuyển dụng qua tìm cv
    @ManyToMany(mappedBy = "recruiters")
    @Builder.Default
    List<Intern> interns = new ArrayList<>();

    public void addPostJob(PostJob postJob) {
        postJob.setRecruiter(this);
        postJobs.add(postJob);
    }

    public Recruiter(String email, String firstName, String lastName, Role role, AuthProvider provider) {
        super(email, firstName, lastName, role, provider);
    }

    @Override
    public String toString() {
        return "Recruiter{} " + super.toString();
    }
    public void setCompany(Company company) {
        company.setRecruiter(this);
        this.company = company;
    }

    public void recruitIntern(Intern intern) {
        this.interns.add(intern);
        intern.getRecruiters().add(this);
    }
}
