package com.jobportal.repostory;

import com.jobportal.entity.Company;
import com.jobportal.entity.Intern;
import com.jobportal.model.InternshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {


    @Query("SELECT c FROM Company c WHERE c.checkInformation = TRUE AND c.operated = true ")
    Page<Company> getCompaniesApproved(Pageable pageable);
}
