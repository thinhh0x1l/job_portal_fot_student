package com.jobportal.entity;

import com.jobportal.model.Faculty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Lecturers")
public class Lecturer extends User {

    @Enumerated(EnumType.STRING)
    Faculty faculty;
    @OneToMany(mappedBy = "lecturer",fetch = FetchType.EAGER)
    List<Intern> interns = new ArrayList<>();

    public void addIntern(Intern intern) {
        interns.add(intern);
        intern.setLecturer(this);
    }
    public void removeIntern(Intern intern) {
        intern.setLecturer(null);
        interns.remove(intern);
    }
}
