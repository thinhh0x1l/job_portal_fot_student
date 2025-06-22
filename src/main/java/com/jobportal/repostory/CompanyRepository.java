package com.jobportal.repostory;

import com.jobportal.entity.Company;
import com.jobportal.entity.Intern;
import com.jobportal.model.District;
import com.jobportal.model.InternshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Integer> {


    @Query("SELECT c FROM Company c WHERE c.checkInformation = TRUE AND c.operated = true ")
    Page<Company> getCompaniesApproved(Pageable pageable);


    @Query("SELECT c FROM Company c " +
            "WHERE c.checkInformation = TRUE " +
            "AND c.operated = TRUE " +
            "AND c.district = :district " +
            "AND LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Company> getCompaniesByDistrict(@Param("keyword") String keyword,
                               @Param("district") District district,
                               Pageable pageable);

    @Query("SELECT c FROM Company c " +
            "WHERE c.checkInformation = TRUE " +
            "AND c.operated = TRUE " +
            "AND LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Company> getCompanies(@Param("keyword") String keyword,
                               Pageable pageable);


    @Query("""
    SELECT c, COUNT(DISTINCT j), AVG(DISTINCT cm.star)
    FROM Company c
    LEFT JOIN PostJob j ON j.recruiter.company = c
    LEFT JOIN Comment cm ON cm.company = c
    WHERE c IN :companies AND j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > :expire
    GROUP BY c
    ORDER BY COUNT(j) DESC
""")
    List<Object[]> getCompaniesWithJobCountAndAverageStarDESC(@Param("companies") List<Company> companies,
                                                              @Param("expire") LocalDate expire);
    @Query("""
    SELECT c, COUNT(DISTINCT j), AVG(DISTINCT cm.star)
    FROM Company c
    LEFT JOIN PostJob j ON j.recruiter.company = c
    LEFT JOIN Comment cm ON cm.company = c
    WHERE c IN :companies AND j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > :expire
    GROUP BY c
    ORDER BY COUNT(j) ASC
""")
    List<Object[]> getCompaniesWithJobCountAndAverageStarASC(@Param("companies") List<Company> companies,
                                                             @Param("expire") LocalDate expire);

    @Query("""
    SELECT c, COUNT(DISTINCT j), AVG(DISTINCT cm.star)
    FROM Company c
    LEFT JOIN PostJob j ON j.recruiter.company = c
    LEFT JOIN Comment cm ON cm.company = c
    WHERE c IN :companies AND j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > :expire
    GROUP BY c
    ORDER BY AVG(cm.star) DESC
""")
    List<Object[]> getCompaniesOrderByAverageStarDesc(@Param("companies") List<Company> companies,
                                                      @Param("expire") LocalDate expire);

    @Query("""
    SELECT c, COUNT(DISTINCT j), AVG(DISTINCT cm.star)
    FROM Company c
    LEFT JOIN PostJob j ON j.recruiter.company = c
    LEFT JOIN Comment cm ON cm.company = c
    WHERE c IN :companies AND j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > :expire
    GROUP BY c
    ORDER BY AVG(cm.star) ASC 
""")
    List<Object[]> getCompaniesOrderByAverageStarASC(@Param("companies") List<Company> companies,
                                                     @Param("expire") LocalDate expire);

    @Query("""
    SELECT c, COUNT(DISTINCT j), AVG(DISTINCT cm.star)
    FROM Company c
    LEFT JOIN PostJob j ON j.recruiter.company = c
    LEFT JOIN Comment cm ON cm.company = c
    WHERE c IN :companies AND j.approved = TRUE AND j.hidden = FALSE  AND j.postingDeadline > :expire
    GROUP BY c
""")
    List<Object[]> getCompaniesWithoutOrdering(@Param("companies") List<Company> companies,
                                               @Param("expire") LocalDate expire);


}
