package com.jobportal.entity;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String url;
    String image;
    @Lob
    String content;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    boolean seen = false;
    LocalDateTime created = LocalDateTime.now();

    public String getImageee(){
        if(sender==null)
            return "";
        return "user-photos/"+sender.getId()+"/"+sender.getImageUrl();
    }

    public String getImageCom(){
        Recruiter r = (Recruiter) sender;
        return "/images/companies/"+r.getId()+"/"+r.getCompany().getImageUrl();
    }
}
