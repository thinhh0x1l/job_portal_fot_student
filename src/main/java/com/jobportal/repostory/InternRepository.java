package com.jobportal.repostory;

import com.jobportal.entity.Intern;
import com.jobportal.entity.Recruiter;
import com.jobportal.model.InternshipStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InternRepository extends JpaRepository<Intern, Integer> {
    @Query("SELECT r FROM Intern r WHERE r.email = ?1")
    Optional<Intern> findByEmail(String email);

    @Query("SELECT r FROM Intern r WHERE r.verifyToken = ?1")
    Optional<Intern> findByVerifyToken(String verifyToken);



    @Query("SELECT i FROM Intern i LEFT JOIN i.favorites WHERE i.id = :id")
    Optional<Intern> findByIdWithFavorites(@Param("id") Integer id);

    @Query("SELECT CASE WHEN (COUNT(i) > 0) THEN true ELSE false END " +
            "FROM Intern i " +
            "WHERE i.id = ?1 AND i.company IS NULL")
    boolean existsByIdAndCompanyIsNull(Integer id);


    @Query("SELECT i FROM Intern i WHERE i.company.id = :id AND i.status NOT IN :internShipStatus AND " +
            "(LOWER(i.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(CONCAT(i.firstName ,' ' ,i.lastName)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Intern> findInternsByCompany(@Param("id") Integer companyId,
                                      @Param("internShipStatus") List<InternshipStatus> status,
                                      @Param("keyword") String keyword,
                                      Pageable pageable);

    @Query("SELECT i FROM Intern i WHERE i.company.id = :id AND i.status = :internShipStatus")
    List<Intern> getInternsByIn(@Param("id") Integer companyId,
                                @Param("internShipStatus") InternshipStatus status);

    @Query("SELECT i FROM Intern i WHERE i.lecturer.id = :id")
    List<Intern> getAllInternsByLecturerId(@Param("id")Integer lecturerId);

    @Query("SELECT i FROM Intern i WHERE i.company.id = :id ")
    List<Intern> findInternsByCompany(@Param("id") Integer companyId);

}
