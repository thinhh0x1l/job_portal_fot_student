package com.jobportal.entity;

import com.jobportal.model.CompanySize;
import com.jobportal.model.District;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "Companies")
public class Company {
    @Id
    Integer id;
    String companyName;
    String taxCode;
    String website;
    String numberPhone;
    String email;
    String address;
    @Enumerated(EnumType.STRING)
    CompanySize companySize;
    @Enumerated(EnumType.STRING)
    District district;
    @Builder.Default
    boolean operated = true;
    String imageUrl;
    @Lob
    String description;
    String certificate;
    Boolean checkCertificate;

    LocalDateTime dateInformationUpdated;
    Boolean checkInformation;

    @OneToMany(mappedBy = "company")
    List<Comment> comments = new ArrayList<>();


    @JoinColumn(name = "id")
    @MapsId
    @OneToOne
    Recruiter recruiter;

    @Builder.Default
    @OneToMany(fetch = FetchType.EAGER)
    List<Intern> interns = new ArrayList<>();

    public void addIntern(Intern intern) {
        interns.add(intern);
        intern.setCompany(this);
    }

//    public String duyet(){
//        if(checkCertificate == null && checkInformation == null){
//            return "Chưa cập nhật thông tin";
//        }
//        if(checkCertificate == null || checkInformation == null){}
//        if(checkCertificate&&checkInformation)
//            return "Đã duyệt";
//        if()
//    }

    public String trangThai(){
        if(checkCertificate == null && checkInformation == null) {
            if (taxCode == null)
                return "status-gray";
            return "status-inactive";
        }
        if(checkCertificate != null && checkInformation != null && !checkCertificate && !checkInformation)
            return "status-pending";
        if(checkCertificate != null && checkInformation != null && !checkCertificate)
            return "status-pending";
        if(checkCertificate != null && checkInformation != null && !checkInformation)
            return "status-pending";
        if(checkCertificate != null&& !checkCertificate)
            return "status-pending";
        if(checkInformation != null && !checkInformation)
            return "status-pending";
        return "status-active";
    }
    public String color(){
        if(checkCertificate == null && checkInformation == null) {
            if (taxCode == null)
                return "Chưa cập nhật thông tin";
            return "Từ chối";
        }
        if(checkCertificate != null && checkInformation != null && !checkCertificate && !checkInformation)
            return "Chờ duyệt 2";
        if(checkCertificate != null && checkInformation != null && !checkCertificate)
            return "Chờ duyệt 1";
        if(checkCertificate != null && checkInformation != null && !checkInformation)
            return "Chờ duyệt 1";
        if(checkCertificate != null&& !checkCertificate)
            return "Chờ duyệt 1";
        if(checkInformation != null && !checkInformation)
            return "Chờ duyệt 1";
        return "Đã duyệt";
    }
    public boolean checkAp(){
        if(checkCertificate == null && checkInformation == null) {
            if (taxCode == null)
                return false;
            return false;
        }
        if(checkCertificate != null && checkInformation != null && !checkCertificate && !checkInformation)
            return false;
        if(checkCertificate != null && checkInformation != null && !checkCertificate)
            return false;
        if(checkCertificate != null && checkInformation != null && !checkInformation)
            return false;
        if(checkCertificate != null&& !checkCertificate)
            return false;
        if(checkInformation != null && !checkInformation)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Company [id=" + id + ", companyName=" + companyName + ", taxCode=" + taxCode;
    }
}
