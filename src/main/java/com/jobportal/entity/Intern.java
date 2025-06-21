package com.jobportal.entity;

import com.jobportal.model.InternshipStatus;
import com.jobportal.model.Review;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Interns")
public class Intern extends User {
    @Enumerated(EnumType.STRING)
    @Builder.Default
    Review reviewOfLecturer = Review.CHUA_XEP_LOAI;
    Double pointInternShip;


    String imageCover;
    LocalDate dateToInternShip;


    @Lob
    String description;

    @OneToMany(fetch = FetchType.EAGER)
    @Builder.Default
    List<ProjectIntern> projectInterns = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    InternshipStatus status;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    List<PostJob> favorites = new ArrayList<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Tag> tags = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "intern")
    List<SendOutCv> sendOutCvs = new ArrayList<>();

    @JoinColumn(name = "company_id")
    @ManyToOne
    Company company;

    @JoinColumn(name = "lecturer_id")
    @ManyToOne
    Lecturer lecturer;

    @OneToOne(mappedBy = "intern")
    PostCv postCv;

    ///Được các nhà tuyển dụng săn đón
    @ManyToMany(fetch = FetchType.EAGER)
    @Builder.Default
    List<Recruiter> recruiters = new ArrayList<>();

    /// Nhận task
    @Builder.Default
    @OneToMany(mappedBy = "intern",fetch = FetchType.EAGER)
    List<Task> tasks = new ArrayList<>();

    public void addPostCv(PostCv postCv) {
        this.postCv = postCv;
        postCv.setIntern(this);
    }

    public void addFavorites(PostJob postJob) {
        this.favorites.add(postJob);
        postJob.getInterns().add(this);
    }
    public void removeFavorites(PostJob postJob) {
        this.favorites.remove(postJob);
        postJob.getInterns().remove(this);
    }

    public String getMSSV(){
        return getEmail().split("@")[0];
    }
    public String getLop(){
        return getEmail().split("0")[0]+getEmail().split("0")[1];
    }

}
