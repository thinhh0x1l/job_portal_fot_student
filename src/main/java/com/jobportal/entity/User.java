package com.jobportal.entity;

import com.jobportal.model.AuthProvider;
import com.jobportal.model.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;

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
@Table(name = "Users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(unique = true, nullable = false)
    String email;
    String password;
    String firstName;
    String lastName;
    @Enumerated(EnumType.STRING)
    Role role;
    String imageUrl;
    boolean enabled;
    String verifyToken;
    String resetPwdToken;
    @Enumerated(EnumType.STRING)
    AuthProvider provider;

    String phoneNumber;

    boolean sendEmail = true;

    @OneToMany(mappedBy = "user")
    List<Comment> comments = new ArrayList<>();


    @ManyToMany(fetch = FetchType.EAGER)
    Set<Comment> likedComments = new HashSet<>();

    @ManyToMany(mappedBy = "users")
    List<PostJob> postJobs = new ArrayList<>();

    public User(String email, String firstName, String lastName, Role role, AuthProvider provider) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.provider = provider;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", role=" + role +
                ", imageUrl='" + imageUrl + '\'' +
                ", enabled=" + enabled +
                ", verifyToken='" + verifyToken + '\'' +
                ", resetPwdToken='" + resetPwdToken + '\'' +
                ", provider=" + provider +
                '}';
    }
}
