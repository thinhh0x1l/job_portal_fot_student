package com.jobportal.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @JoinColumn(name = "user_id")
    @ManyToOne
    User user;

    @JoinColumn(name = "company_id")
    @ManyToOne
    Company company;

    @ManyToMany(mappedBy = "likedComments")
    Set<User> likedByUser = new HashSet<>();

    LocalDateTime timeCreated = LocalDateTime.now();
    String title;
    String content;

    int likeF;
    int star;

}
