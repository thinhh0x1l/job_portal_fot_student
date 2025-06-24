package com.jobportal.entity;

import com.jobportal.model.Major;
import com.jobportal.model.Salary;
import com.jobportal.security.CustomUserDetails;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Post_Jobs")
public class PostJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String name;
    @Lob
    String description;
    double salary;
    @Lob
    String candidateRequirements;
    @Lob
    String benefits;
    LocalDateTime timePost;
    LocalDateTime updatedTime;
    LocalDate postingDeadline;

    @Builder.Default
    int numberOfApplications = 1;
    int view;
    @Column(name = "isUrgent")
    boolean urgent;
    @Builder.Default
    boolean approved = false; // chờ duyệt
    @Builder.Default
    boolean hidden =false;
    @Enumerated(EnumType.STRING)
    Major major;

    @JoinColumn(name = "recruiter_id")
    @ManyToOne
    Recruiter recruiter;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER,mappedBy = "favorites")
    Set<Intern> interns = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "postJob")
    List<SendOutCv> sendOutCvs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    Set<User> users = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    Set<Tag> tags = new HashSet<>();

    public boolean isFavorited(Object principal) {
        CustomUserDetails userDetails = (CustomUserDetails)principal;
        for (Intern intern : interns) {
            if(Objects.equals(intern.getId(), userDetails.getId())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public String toString() {
        return "PostJob{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", salary=" + salary +
                ", candidateRequirements='" + candidateRequirements + '\'' +
                ", benefits='" + benefits + '\'' +
                ", timePost=" + timePost +
                ", postingDeadline=" + postingDeadline +
                ", numberOfApplications=" + numberOfApplications +
                ", view=" + view +
                ", isUrgent=" + urgent +
                ", approved=" + approved +
                ", major=" + major +
                ", recruiter=" + recruiter +
                '}';
    }

    public boolean checkSalary(String sala, double salary){
        Salary salary1 = Salary.valueOf(sala);
        return salary >= salary1.getMin() && salary < salary1.getMax();
    }

    public String formatDescription(String raw,int color) {
        String[] lines = raw.split("\n");
        StringBuilder sb = new StringBuilder();
        String col = switch (color){
            case 1 -> "blue";
            case 2 -> "green";
            case 3 -> "amber";
            default -> "purple";
        };


        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\..*")) {
                sb.append("<div class='mt-4 mr-3'><span style='font-weight:bold; color:#1D4ED8;'>")
                        .append(line)
                        .append("</span></div>");
            }else if( line.startsWith("-")){
                sb.append("<div class='flex items-start mb-2'><i class='fas fa-circle text-")
                        .append(col).append("-500 text-xs mt-2 mr-2'></i>")
                        .append(line.substring(1))
                        .append("</div>");
            }else {
                sb.append("<div class='mt-1 mr-1'>").append(" ").append(line).append("</div>");
            }
        }

        return sb.toString();
    }

    public String statusV1(){
         if(postingDeadline.isBefore(LocalDate.now()))
            return "HẾT HẠN";
        else if(hidden && !approved)
            return "BỊ TỪ CHỐI";
        else if(hidden)
            return "GỠ BÀI";
        else if(approved)
            return "ĐANG HOẠT ĐỘNG";
        return"CHỜ DUYỆT";
    }
    public int statusV2(){
        if(postingDeadline.isBefore(LocalDate.now()))
            return 3;
        else if(hidden && !approved)
            return 5;
        else if(hidden)
            return 4;
        else if(approved)
            return 1;
        return 2;
    }
    public String colorV1(){

        if(postingDeadline.isBefore(LocalDate.now()))
            return "bg-slate-100 text-slate-700 border-l-4 border-slate-500";
        else if(hidden && !approved)
            return "bg-red-50 text-red-700 border-l-4 border-red-500";
        else if(hidden)
            return "bg-slate-100 text-slate-700 border-l-4 border-slate-500";
        else if(approved)
            return "bg-green-50 text-green-700 border-l-4 border-green-500";
        return"bg-yellow-50 text-yellow-700 border-l-4 border-yellow-500";
    }



    /// Admin/postjob
    public String colorV3(){
        if(hidden&& !approved)
            return "px-2 inline-flex text-xs leading-5 font-semibold rounded-full status-inactive";
        if(approved)
            return "px-2 inline-flex text-xs leading-5 font-semibold rounded-full status-active";
        return "px-2 inline-flex text-xs leading-5 font-semibold rounded-full status-pending";
    }
    public String statusV3(){
        if(hidden&& !approved)
            return "Từ Chối";
        if(approved)
            return "Đã duyệt";
        return "Chưa duyệt";
    }

    public int statusV4(){
        if(hidden&& !approved)
            return 2;
        if(approved)
            return 1;
        return 0;
    }

    public String formatSalary(){
        if(salary>=1)
            return salary + " triệu";
        else if(salary!=0)
            return salary*1000000 + " nghìn";
        return "Không lương";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostJob postJob = (PostJob) o;
        return Objects.equals(id, postJob.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
