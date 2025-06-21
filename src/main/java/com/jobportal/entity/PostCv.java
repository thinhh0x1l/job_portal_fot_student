package com.jobportal.entity;

import com.jobportal.model.Major;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Post_Cvs")
public class PostCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String fileCvUrl;
    String title;
    String description;

    LocalDateTime timePost;
    @Enumerated(EnumType.STRING)
    Major major;

    @JoinColumn(name = "id")
    @MapsId
    @OneToOne
    Intern intern;

}
