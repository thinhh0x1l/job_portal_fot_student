package com.jobportal.entity;


import com.jobportal.model.CVStatus;
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
@Table(name = "Send_Out_Cvs")
public class SendOutCv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "intern_id")
    Intern intern;

    @ManyToOne
    @JoinColumn(name = "post_jobs_id")
    PostJob postJob ;
    String fullname;
    String phoneNumber;
    String email;
    @Lob
    String introduction;
    String fileCvUrl;
    LocalDateTime timeSent;

    String url;

    @Builder.Default
    boolean seen = false;
    @Enumerated(EnumType.STRING)
    CVStatus cvStatus;

    public void setIntern(Intern intern) {
        this.intern = intern;
        intern.getSendOutCvs().add(this);
    }
    public void setPostJob(PostJob postJob) {
        this.postJob = postJob;
        postJob.getSendOutCvs().add(this);
    }
    public boolean process(CVStatus cvStatus) {
        if(cvStatus.equals(CVStatus.BEING_INTERVIEWED)||
                cvStatus.equals(CVStatus.HIRED)||
                cvStatus.equals(CVStatus.SUCCESS))
            return true;
        return false;
    }
    public String process1(CVStatus cvStatus) {
        if(cvStatus.equals(CVStatus.HIRED)||
                cvStatus.equals(CVStatus.SUCCESS))
            return "text-green-800";
        else if(cvStatus.equals(CVStatus.REJECTED))
            return "text-red-800";

        return "text-gray-400";
    }
    public String process2(CVStatus cvStatus) {
        return switch (cvStatus){
            case REJECTED -> "Bị từ chuối";
            case HIRED -> "Được tuyển dụng";
            case SUCCESS -> "Chờ phản hồi";
            default -> "Chưa có kêt quả";
        };

    }
    public String process3(CVStatus cvStatus) {
        return switch (cvStatus){
            case REJECTED -> "bg-red-500";
            case HIRED -> "bg-green-500";
            case SUCCESS -> "bg-yellow-500";
            default -> "bg-gray-300";
        };

    }

}
