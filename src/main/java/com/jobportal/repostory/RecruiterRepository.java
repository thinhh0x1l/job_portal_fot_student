package com.jobportal.repostory;

import com.jobportal.entity.Company;
import com.jobportal.entity.Intern;
import com.jobportal.entity.PostJob;
import com.jobportal.entity.Recruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RecruiterRepository extends JpaRepository<Recruiter, Integer> {

    @Query("SELECT r FROM Recruiter r WHERE r.email = ?1")
    Optional<Recruiter> findByEmail(String email);

    @Query("SELECT r FROM Recruiter r WHERE r.verifyToken = ?1")
    Optional<Recruiter> findByVerifyToken(String verifyToken);

    boolean existsByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END FROM" +
            " Recruiter r WHERE r.id = ?1 AND r.company IS NOT NULL")
    boolean existsByCompany(Integer id);

    @Query("SELECT r FROM Recruiter r WHERE "+
            "  r.company.operated=TRUE "+
            "AND  r.company.checkInformation=TRUE "+
            "AND  r.company.checkCertificate=TRUE "+
            "AND  r.enabled = TRUE ")
    List<Recruiter> recruitersByCompany();
}
